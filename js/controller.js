/**
 * Created by 前程 on 2015/11/25.
 */
//socket io for chat
var rest_server ="http://192.168.0.179:8080/rest";
var chat_server = "http://192.168.0.179:9092";
var socket = io.connect(chat_server, {reconnectionAttempts:3,reconnectionDelay: 3000, timeout: 30000});
var uuid= 'user_' + Math.uuid(6,10);
socket.emit('login',uuid);
console.log(uuid + ' login');

robot = angular.module('robot', ['ngSanitize'])
    .controller('ChatCtrl', function ($http, $scope,$timeout) {
        socket.on('newmessage',function(msg){
            $scope.$apply(function(){
                msg.html=generateChatBubble(msg);
                $scope.msghistory.push(msg);

                $timeout(function(){
                    var height = $("#chatcontent")[0].scrollHeight;
                    $("#chatcontent").scrollTop(height);
                },0);
            })
        })
        //variable
        //fake data for product area
        $scope.item = {}
        $scope.item.image = "http://img11.360buyimg.com/n4/jfs/t475/313/935196487/46696/65cec9de/549a3411N0a7ca0a0.jpg"
        $scope.item.url = "http://item.jd.com/1299741.html"
        $scope.item.title = "华为荣耀盒子 真4K超高清H.265硬解 网络机顶盒 蓝光3D安卓电视盒子 无线300M"

        //data for suggestion
        $scope.suggestion = [];
        $scope.showSugg = false;
        for(var i=0;i<6;i++){
            $scope.suggestion.push({
                text: '我是suggestion' + i,
                link: 'http://www.baidu.com'
            })
        }

        var welcome='您好，我是机器人小明，有什么可以帮您!';
        $scope.msghistory=[];

        $scope.isRobotService=true;

        //event functions
        $scope.getSuggestion = function(){
         if($scope.msg.text.length >= 2){
             $scope.showSugg = true;
         }
         else{
             $scope.showSugg = false;
         }
        };
        $scope.clickSuggestion = function(sugg){
            var msg={
                text:sugg.text,
                from:'user',
                to:'robot'
            }
            msg.html = generateChatBubble(msg);
            $scope.msghistory.push(msg)
            $scope.showSugg = false;
            $scope.msg.text='';
        }

        //data for chat box
        var wel_msg={
            text:welcome,
            from:"robot",
            to:"user123"
        }
        wel_msg.html=generateChatBubble(wel_msg);
        $scope.msghistory.push(wel_msg);

        $scope.Enter = function(event){
            if(event.keyCode == 13){
                send($scope.msg.text);
            }
        }
        $scope.sendMessage = function(){
            send($scope.msg.text);
        };

        //animation
        $scope.changeArrowPos = function(idx,total){
            var width=$(".side-nav-item:first").css("width")
            width=width.substr(0,width.length-2)
            $('.tab_arrow').css('left',(idx * width)+'px');

            var idsel='#nav'+idx
            $(idsel).css('display','block')
            for (var i=0;i<total;i++){
                if(i != idx)
                    $('#nav'+i).css('display','none');
            }
        }

        //swith to human
        $scope.tips='很高兴，机器人小明为你服务';
        $timeout(function(){
            $scope.tips='';
        },5000);
        $scope.switchToHuman = function(){
            //1. request for switching to human service
            var human = requestHumanService();
            if(human){
                $scope.tips=human.id + '号客服' + human.name + '为您服务';
                $scope.isRobotService = false;
            }else{
                $scope.tips='人工坐席忙，稍候再试吧!';
                $scope.isRobotService = true;
            }
            $timeout(function(){
                $scope.tips='';
            },5000);
        }

        //inline function
        function send(text){
            var curmsg={
                text:text,
                from:uuid,
                to:"robot"
            }
            socket.emit('newmessage',curmsg);
            console.log('send:' + curmsg);
            curmsg.html = generateChatBubble(curmsg);
            $scope.msg.text="";
            $scope.msghistory.push(curmsg);
            $scope.showSugg = false;

            $timeout(function(){
                var height = $("#chatcontent")[0].scrollHeight;
                if (height > $("#chatcontent").height()){
                    $(".nano").nanoScroller();
                    $(".nano-pane").css('display','block');
                    $("#chatcontent").scrollTop(height);
                }
            },0);


        }
        function generateChatBubble(msg){
            var isrobot = (msg.from=='robot');
            var iscustomer = (msg.from.indexOf('user')==0);
            var robotclass=iscustomer?'customer':'robot';

            var avatar='';
            if (isrobot)
                avatar = '<div class="header_img robotimg fl"><div class="avatar_img_mask"></div></div>';
            else if(iscustomer)
                avatar = '<div class="header_img customerimg"><div class="avatar_img_mask"></div></div>';
            else avatar =  '<div class="header_img humanimg fl"><div class="avatar_img_mask"></div></div>';
            var top='<div class="'+ robotclass +' clearfix">'+ avatar +'<table class="msg" cellpadding="0" cellspacing="0"><tbody><tr><td class="lt"></td><td class="mt"></td><td class="rt"></td></tr>';
            var middle='<tr><td class="lm"><span></span></td><td class="mm">'+ msg.text +'</td><td class="rm"><span></span></td></tr>';
            var bottom='<tr><td class="lb"></td><td class="mb"></td><td class="rb"></td></tr></tbody></table></div>';
            return top + middle + bottom;
        }
        function requestHumanService(){
            var url=rest_server+'gethuman';
            $http.get(url).then(function(response){
                var human=response.data;
                return human;

            },function(error){
                console.log(error)
                return null;
            })
        }
    })
