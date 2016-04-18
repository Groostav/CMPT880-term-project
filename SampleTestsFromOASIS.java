
// Functional automated test against component, 7 lines of reasonably concise code.
@Test
public void followed_by_running_state_determineIfOptimizationContinues_should_recurse_once_and_return_true(){
   //setup
   OptimizerSynchronizationMarshaller marshaller = makeMarshaller();
   when(stateMachine.getState()).thenReturn(State.Pending, State.Running);

   //act
   boolean shouldContinue = marshaller.determineIfOptimizationContinues();

   //assert
   assertThat(shouldContinue).isTrue();
   assertThat(marshaller.acknowledgements).containsExactly(pendingState);
   verify(stateMachine, twice()).getState();
}

// Atomicity (non-functional) test against the same component. 
// significanty more code, author thought it warranted substantial documentation and a timeout.
// cyclomatic complexity is an order of magnitude higher.

/**
 * This is a test to assert that the scheme handles contention between the optimizer and the HMI thread
 * in the Start-Order state well. In this state, whether we transfer back to Idle or on to Running
 * is determined by a 'race'
 * (first order to be recieved -- more specifically the first one to lock the state machine)
 * between these two threads. This test asserts that, in one scenario, the locks work out correctly.
 */
@Test(timeout = 1000)
public void when_in_state_StartPending_and_a_StopOrder_happens_immediately_before_the_optimizer_tries_to_ack_should_become_idle() throws InterruptedException {
    //setup
    marshaller = makeMarshaller();
    marshaller.getStateMachine().currentState = StartPending;

    CountDownLatch stopOrderShouldContinueSignal = new CountDownLatch(1);
    CountDownLatch stopOrderIsReadyToContinueSignal = new CountDownLatch(1);
    CountDownLatch optimizerIsChecking = new CountDownLatch(1);

    LinqingList<Transfer> reportedTransfers = new LinqingList<>();

    marshaller.getStateMachine().addTransferListener((oldState, xfer, newState) -> {

        reportedTransfers.add(xfer);

        if(xfer == Transfer.StopOrder){
            //blocks us while we're in the middle of transferring, a vary precarious spot!
            stopOrderIsReadyToContinueSignal.countDown();
            ExceptionUtilities.failOnException(stopOrderShouldContinueSignal::await);
        }
    });

    Runnable hmiWorkload = () -> {
        marshaller.issueOrderAndAwaitAcknowledgement(Transfer.StopOrder);
    };

    Runnable optimizerWorkload = () -> {
        optimizerIsChecking.countDown();
        boolean shouldContinue = marshaller.determineIfOptimizationContinues();
        assertThat(shouldContinue).isFalse();
    };

    //act I : get HMI thread ordering change-back to stop
    Thread fauxHMIThread = syncingUtilities.asynchronously("fauxHMIThread", ThreadTag.HMI, hmiWorkload).get();
    stopOrderIsReadyToContinueSignal.await();

    //act II : get optimizer asking if it should continue while HMI thread is transferring states
    Thread fauxOptimizer = syncingUtilities.asynchronously("fauxOptimizer", ThreadTag.Optimizer, optimizerWorkload).get();
    optimizerIsChecking.await();
    syncingUtilities.sleepUnlessInterruptedFor(CheckDelay); //let optimizer *attempt* to continue
    Thread.State optimizerStateAfterChecking = fauxOptimizer.getState();

    //act III : let the HMI thread finish its transfer
    stopOrderShouldContinueSignal.countDown();
    fauxHMIThread.join();
    fauxOptimizer.join();

    //assert
    assertThat(marshaller.getState()).isEqualTo(Idle);
    assertThat(reportedTransfers).containsExactly(StopOrder);
    assertThat(optimizerStateAfterChecking).isEqualTo(Thread.State.BLOCKED);
    eventBus.shouldNotHaveBeenAskedToPost(OptimizationRunGroupHaltedEvent.class); //remember this test is pending -> stopped
}
