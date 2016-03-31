var im = function()
{
  var _username;
  var _chatSubscription;
  var _metaSubscriptions = [];
  var _handshook = false;
  var _connected = false;
  var _cometd;
  var _alert = [];
  var _cId;
  
  var elem = '';
  
  return  {
    /**
     * 初始化
     */
    init : function()
    {

      // _comet = new $.Cometd(); // Creates a new Comet object
    _cometd = $.cometd; // Uses the default Comet object

    $('#login_panel').hide();
    $('#chat').hide();
    $('#fd_panel').show();
    $('#name').focus();

    // 关闭聊天窗口
    $('#close').click( function(e)
    {
      $('#chat').hide();
    })
    join();
    // 登录
//    $('#login').click( function(e)
//    {
//      join();
//      e.preventDefault();
//    });

    
    // 离开
    $('#leave').click(leave);

    // 回车登录
//    $('#name').attr(
//    {
//      'autocomplete' :'OFF'
//    }).keyup( function(e)
//    {
//      if (e.keyCode == 13)
//      {
//        join();
//        e.preventDefault();
//      }
//    });

    // 发送信息
    $('#youSay').keyup( function(e)
    {
      if (e.keyCode == 13)
      {
        send();
        e.preventDefault();
      }
    });

    // 发送信息（按钮事件绑定）
    $('#im_send').click(send);

    // 群发送信息（按钮事件绑定）
    $('#group_send').click(allSend);
    
    $('#chat_history').click(getChatHistory);
    // 离开
    $(window).unload(function(){ leave(); });
    
    applyDrop();
  },

  /**
   * 和谁聊天
   */
  sayTo : function(target, id, isGroup)
  {
    // debugger;
    var ret = $(target).text();
    var index = $('#friendList a').index(target);
    $('#toFriend').text(ret);
    $('#toFriend').attr("cid", id);
    $('#chat').show();
    $('#showBox div').hide();
    $('#' + id +'[isGroup=' + isGroup + ']').show();
    window.clearInterval(_alert[index]);
    $(target).removeClass("alert");
    _alert[index] = null;
    
    if (isGroup == 'true') {
    	$("#state_panel").attr("groupId",id);
    } else {
    	$("#state_panel").removeAttr("groupId");	
    }
    var fd = $('#toFriend').attr("cid");
    
    _cometd.publish('/service/readMessage',
            {
              room :'/chat/demos', // This should be replaced by the room name
              receiver :_username,
              sender :ret
            });
  }
  }
  
  /**
   * 退订频道
   */
  function _chatUnsubscribe()
  {
    if (_chatSubscription)
      _cometd.unsubscribe(_chatSubscription);
    _chatSubscription = null;
  }
  
/**
 *订阅频道 
 */
  function _chatSubscribe()
  {
    _chatUnsubscribe();
    _chatSubscription = _cometd.subscribe('/chat/demos', this, function(e)
    {
      receive(e);
           
      var fromUser = e.data.user;
      var membership = e.data.join || e.data.leave;
      var text = e.data.chat;

      // 来信息提示
        //if (e.data.scope == 'private' && _cId != e.data.sender)
      if (!membership && _username != e.data.user)
        {

          $('#friendList a').each(
              function(n)
              {
                if ($(this).text() == e.data.user && !_alert[n]
                    && e.data.user != $('#toFriend').text())
                {
                  _alert[n] = window.setInterval("$($('#friendList a')[" + n
                      + "]).toggleClass('alert')", 500);
                }
              })
        }
      });// receive);
  }

  /**
   * 移除监听
   */
  function _metaUnsubscribe()
  {
    $.each(_metaSubscriptions, function(index, subscription)
    {
      _cometd.removeListener(subscription);
    });
    _metaSubscriptions = [];
  }

  /**
   * 注册监听（握手，连接）
   */
  function _metaSubscribe()
  {
    _metaUnsubscribe();
    _metaSubscriptions.push(_cometd.addListener('/meta/handshake', this,
        _metaHandshake));
    _metaSubscriptions.push(_cometd.addListener('/meta/connect', this,
        _metaConnect));
  }

  /**
   * 握手
   */
  function _metaHandshake(message)
  {
    _handshook = message.successful;
    _connected = false;
    _cId = message.clientId;
    /*
     * receive({ data: { user: '系统信息', join: true, chat: '握手 ' + (_handshook ?
     * '成功' : '失败') } });
     */
  }

  /**
   * 连接
   */
  function _metaConnect(message)
  {
    var wasConnected = _connected;
    _connected = message.successful;
    if (wasConnected)
    {
      if (_connected)
      {
        // Normal operation, a long poll that reconnects
      } else
      {
        // Disconnected
        receive(
        {
          data :
          {
            user :'系统信息',
            join :true,
            chat :'断开连接'
          }
        });
      }
    } else
    {
      if (_connected)
      {
        receive(
        {
          data :
          {
            user :'系统信息',
            join :true,
            chat :'连接成功！'
          }
        });
        _cometd.startBatch();
        _chatSubscribe();
        _cometd.publish('/chat/demos',
        {
          user :_username,
          join :true,
          chat :_username + ' 已上线'
        });
        _cometd.endBatch();
      } else
      {
        // Could not connect
        receive(
        {
          data :
          {
            user :'系统信息',
            join :true,
            chat :'无法连接'
          }
        });
      }
    }
  }

  /**
   * 登录
   */
  function join()
  {

	_username = $.cookie("userName");
	
    if (!_username)
    {
      alert('请输入用户名!');
      return;
    }
    var cometURL = document.location.protocol + '//' + document.location.hostname + ':'
        + document.location.port + '/im/cometd';

    _metaSubscribe();
    _cometd.init(cometURL);
    
    $('#login_panel').hide();
    $('#fd_panel').show();
    $('#youSay').focus();
    $("#my_name").text(_username);
  }

  /**
   * 离开
   */
  function leave()
  {
    if (!_username)
      return;

    _cometd.startBatch();
    _cometd.publish('/chat/demos',
    {
      user :_username,
      leave :true,
      chat :_username + ' 已下线！'
    });
    _chatUnsubscribe();
    _cometd.endBatch();

    _metaUnsubscribe();

    // switch the input form
//    $('#chat').hide();
//    $('#fd_panel').hide();
//    $('#login_panel').show();
//    $('#name').focus();
    _username = null;
    _cometd.disconnect();
//    $('#friendList').html('');
//    $('#showBox').html('');
//    $('#toFriend').html('');
//    $('#system_info').html('');
//    $('#toFriend').removeAttr("cid");
//    $('#historyPanel').hide();
//    $('#historyPanel').html('');
    _cId = null;
    
    window.location.href = "/im/leave";
  }

  /**
   * 发送
   */
  function send()
  {
	if ($("#state_panel").attr("groupId")) {
		groupSend();	
	} else {
		privateSend();
	}
    
  }
  
  /**
   * 点对点发送
   */
  
  function privateSend() {
	  
	    $('#chat').show();
	    var phrase = $('#youSay');
	    var fd = $('#toFriend').attr("cid");
	    var text = phrase.val();
	    var pName = $('#toFriend').text();
	    
	    if (!text || !text.length)
	      return;
	    if(text.length>800)
	    {
	      alert("一次发送信息不得多于800个字！");
	      return;
	    }
	    phrase.val('');
	    $.trim(text);
	    var arr = new Array();
	    var top =text.length/200+1;
	    if(text.length>200)
	    {
	      for(var i=0;i<top;i++)
	      {
	        arr[arr.length]=text.substr(i*200,200);      
	      }
	    }
	    if(arr.length>0)
	    {
	      for(var a=0;a<arr.length;a++)
	      {
	        _cometd.publish('/service/privatechat',
	            {
	              room :'/chat/demos', // This should be replaced by the room name
	              user :_username,
	              chat :arr[a],
//	              peer :fd,
//	              sender :_cId,
	              peerName :pName
	            });       
	      }
	    }
	    else
	    {
	      _cometd.publish('/service/privatechat',
	      {
	        room :'/chat/demos', // This should be replaced by the room name
	        user :_username,
	        chat :text,
//	        peer :fd,
//	        sender :_cId,
	        peerName :pName
	      });
	    }  
	  
  }
  
  /**
   * 发送所有
   */
  function allSend()
  {
    $('#chat').show();
    
    var phrase = $('#youSay');
   // var fd = $('#toFriend').attr("cid");
    var text = phrase.val();
    if (!text || !text.length)
      return;
    phrase.val('');
    _cometd.publish('/service/allchat',
    {
      room :'/chat/demos', // This should be replaced by the room name
      user :_username,
      chat :text
      //peer :fd,
//      sender :_cId
    });
  }
  
  /**
   * 群发送
   */
  function groupSend()
  {
    $('#chat').show();
    
    var groupId = $("#state_panel").attr("groupId");
    
    if (!groupId) {
    	return;
    }
    
    var phrase = $('#youSay');
   // var fd = $('#toFriend').attr("cid");
    var text = phrase.val();
    if (!text || !text.length)
      return;
    phrase.val('');
    _cometd.publish('/service/groupchat',
    {
      room :'/chat/demos', // This should be replaced by the room name
      gId :groupId,
      chat :text,
      user :_username
      //peer :fd,
//      sender :_cId
    });
  }
  
  /**
   * 生成信息
   */
  function receive(message) {
	  var chat = $('#showBox');
	  if (message.data.scope == 'denied') {
	    	// switch the input form
	    	
		    window.location.href = "/im/leave";
		    alert(message.data.chat);
	    	return;
	    } 
	  if (message.data instanceof Array)
	    {
	      var userList = '';
	      var groupList = '';
	      debugger;
	      if (message.data[0].groupUser==true) {
	    	  $.each(message.data, function(index, datum)
		      {
	    		  groupList += '<a class="groupList" href="javascript:void(0)"'
		              + '" onclick="im.sayTo(this,\'' + datum.id + '\',\'true\')">' + datum.name
		              + '</a><br/>';
	    		  chat.append("<div id='" + datum.id + "' isGroup='true'></div>");
			    });
	    	  $('#group_info').html(groupList);
	    	  
	      } else {
	    	  $.each(message.data, function(index, datum)
		      {
	    		  
		    		if (datum.name != _username)
			        {
			          if (datum.state == "online") {
			        	  userList += '<a class="contactList" href="javascript:void(0)" sender="' + datum.id
			              + '" onclick="im.sayTo(this,\'' + datum.name + '\',\'false\')">' + datum.name
			              + '</a>&nbsp;&nbsp;(online)<br/>';
			          } else {
			        	  userList += '<a class="contactList" href="javascript:void(0)" sender="' + datum.id
			              + '" onclick="im.sayTo(this,\'' + datum.name + '\',\'false\')">' + datum.name
			              + '</a>&nbsp;&nbsp;(offline)<br/>';
			          }
			          
			          chat.append("<div id='" + datum.name + "' isGroup='false'></div>");
			        }
			    });
	    	  
              $('#friendList').html(userList);
		      
		      applyDrag();
	       }
	      
	    } else
	    {

	      var sysPanel = $('#system_info');
	      var fromUser = message.data.user;
	      var membership = message.data.join || message.data.leave;
	      var text = message.data.chat;
	      if (!text || (membership && $("#name").val() == fromUser))
	        return;

	      var panel;
	      if (fromUser == _username)
	        panel = $("#" + $('#toFriend').attr("cid"));
	      else
	        panel = $("#" + fromUser);
	      
//	      if (!membership && fromUser == _lastUser)
//	      {
//	          fromUser = '...';
//	      }
//	      else
//	      {
//	        _lastUser = fromUser;
//	      }
	      fromUser += "&nbsp;<span style='font-size:12px;color:#aaa'>"
	          + message.data.time + "</span>&nbsp;";
	      fromUser += '<br/>';
	      
	      if (membership)
	      {
	        sysPanel
	            .append('<span class=\"membership\"><span class=\"text\" style="font-size:12px">' + text + '</span></span><br/>');
	        //_lastUser = '';
	      } 
	      else if (message.data.scope == 'private')
	      {       
	        panel
	            .append('<span style="font-size:12px;color:blue"><span class=\"from\">'
	                + fromUser
	                + '&nbsp;</span><span  style="font-size:12px;color:#000">&nbsp;'
	                + text + '</span></span><br/>');
	      } else
	      {
	        panel.append('<span class=\"from\">' + fromUser
	            + '&nbsp;</span><span class=\"text\">' + text + '</span><br/>');
	      }

	      chat[0].scrollTop = chat[0].scrollHeight - chat.outerHeight();
	      sysPanel[0].scrollTop = sysPanel[0].scrollHeight - sysPanel.outerHeight();
	    }
  }
  function getChatHistory() {
      $('#historyPanel').html('');
		$.ajax({
			   type: "POST",
			   url: "/im/chatHistory",
			   data: "sender=" + _username + "&receiver=" + $('#toFriend').text(),
			   success: function(msg){
			     $('#historyPanel').show();
			     $('#historyPanel').html(msg);
			   }
			 });
	}

  function applyDrag() {
	      $(".contactList").draggable({
		  	helper: 'clone',
		  	opacity: 0.55,
		  	start:function(e, ui)
		      {
		          elem = e.srcElement || e.target;
		      }
		  	});
  }
  
  function applyDrop() {
	  $("#state_panel").droppable({
			accept: ".contactList",
			activeClass: 'droppable-active',
			hoverClass: 'droppable-hover',
			drop: function(ev, ui) 
		    {

		        var userName = $(elem).text();
		        var groupId = $("#state_panel").attr("groupId");
		        
		        if (groupId) {
		        	$.ajax({
		 			   type: "POST",
		 			   url: "/im/group",
		 			   data: "groupId=" + groupId + "&user=" + userName +"&groupOperation=4",//add user to group
		 			   success: function(group){
		        		   if (group) {
		        			   var gp = eval(group)[0];
			        		   if (gp.state == "success") {
				        		   $("#toFriend").append(", " + userName);
				        	   }
		        		   }
		        		   
		 			   }
		 			 });
		        } else {
		        	$.ajax({
		  			   type: "POST",
		  			   url: "/im/group",
		  			   data: "creator=" + _username + "&users=" + userName + "," + $("#toFriend").text()+"&groupOperation=1",
		  			   success: function(group){
		        		  if (group) {
		        			  var gp = eval(group)[0];
			        		  if (gp.state == "success") {
			        			  $("#state_panel").attr("groupId",gp.id);
			        			  $("#toFriend").append(", " + userName);
			      		        
			        		  }
		        		  }
		        		  
		  			   }
		  			 });
		        }
			}
			
		});
  }
}();


