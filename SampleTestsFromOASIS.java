
/**
 * The purpose of this file is to demonstrate the difficulty in concurrency testing. Unit-testing frameworks
 * --and more complex frameworks-- are oriented at expressing the results of functional behaviours,
 * not platform intrinsics, such as concurrency. The result is that trying to write even realtively trivial 
 * parallel-related tests from the standard test frameworks is difficult.
 *
 * This is emphasized by the difference in lines of code, 
 * the use of documentation, and a timeouts, amongst other indicators.
 * The cyclomatic complexity of the second test is an order of magnitude higher.
 *
 * Note also, as per Brian Goetz in 'Java Concurrency in Practice', the second test also contains a subtle bug;
 * the JVM platform is _not_ required to support `Thread.getState()`, as it is meant for a debugging tool only,
 * meaning this test may not run on non OpenJDK based VMs. 
 */
class SimpleTestsFromOASIS{
  
  /////////////////////////////////////////////////
  // Test 1
  
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
  
  ////////////////////////////////////////////////
  // Test 2
  
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
}