package org.itstack.naive.chat.ui;

import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.itstack.naive.chat.ui.view.chat.element.group_bar_friend.ElementFriendLuckUser;
import org.itstack.naive.chat.ui.view.chat.ChatController;
import org.itstack.naive.chat.ui.view.chat.IChatEvent;
import org.itstack.naive.chat.ui.view.chat.IChatMethod;
import org.itstack.naive.chat.ui.view.login.ILoginMethod;
import org.itstack.naive.chat.ui.view.login.LoginController;

import java.util.Date;

/**
 * 窗口          Stage
 * -场景       Scene
 * -布局     stackPane
 * -控件   Button
 */
public class Application extends javafx.application.Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        IChatMethod chat = new ChatController(new IChatEvent() {
            @Override
            public void doQuit() {
                System.out.println("退出操作！");
            }

            @Override
            public void doSendMsg(String userId, String talkId, Integer talkType, String msg, Integer msgType, Date msgDate) {
                System.out.println("发送消息");
                System.out.println("userId：" + userId);
                System.out.println("talkType[0好友/1群组]：" + talkType);
                System.out.println("talkId：" + talkId);
                System.out.println("msg：" + msg);
                System.out.println("msgType[0文字消息/1固定表情]：" + msgType);
            }

            @Override
            public void doEventAddTalkUser(String userId, String userFriendId) {
                System.out.println("填充到聊天窗口[好友] userFriendId：" + userFriendId);
            }

            @Override
            public void doEventAddTalkGroup(String userId, String groupId) {
                System.out.println("填充到聊天窗口[群组] groupId：" + groupId);
            }

            @Override
            public void doEventDelTalkUser(String userId, String talkId) {
                System.out.println("删除对话框：" + talkId);
            }

            @Override
            public void addFriendLuck(String userId, ListView<Pane> listView) {
                System.out.println("新的朋友");
                // 添加朋友
                listView.getItems().add(new ElementFriendLuckUser("1000005", "比丘卡", "05_50", 0).pane());
                listView.getItems().add(new ElementFriendLuckUser("1000006", "兰兰", "06_50", 1).pane());
                listView.getItems().add(new ElementFriendLuckUser("1000007", "Alexa", "07_50", 2).pane());
            }

            @Override
            public void doFriendLuckSearch(String userId, String text) {
                System.out.println("搜索好友：" + text);
            }

            @Override
            public void doEventAddLuckUser(String userId, String friendId) {
                System.out.println("添加好友：" + friendId);
            }
        });


        chat.doShow();
        chat.setUserInfo("1000001", "拎包冲", "02_50");
        // 模拟测试
        chat.addTalkBox(-1, 0, "1000004", "哈尼", "04_50", null, null, false);
        chat.addTalkMsgUserLeft("1000004", "沉淀、分享、成长，让自己和他人都有所收获！", 0, new Date(), true, false, true);
        chat.addTalkMsgUserLeft("1000004", "f_23", 1, new Date(), true, false, true);

        chat.addTalkMsgRight("1000004", "今年过年是放假时间最长的了！", 0, new Date(), true, true, false);

        chat.addTalkBox(-1, 0, "1000002", "铁锤", "03_50", "秋风扫过树叶落，哪有棋盘哪有我", new Date(), false);
        chat.addTalkMsgUserLeft("1000002", "秋风扫过树叶落，哪有棋盘哪有我", 0, new Date(), true, false, true);
        chat.addTalkMsgRight("1000002", "我Q，传说中的老头杀？", 0, new Date(), true, true, false);

        // 群组
        chat.addFriendGroup("5307397", "虫洞技术栈(1区)", "group_1");
        chat.addFriendGroup("5307392", "CSDN 社区专家", "group_2");
        chat.addFriendGroup("5307399", "洗脚城VIP", "group_3");

        // 群组 - 对话框
        chat.addTalkBox(0, 1, "5307397", "虫洞 • 技术栈(1区)", "group_1", "", new Date(), true);
        chat.addTalkMsgRight("5307397", "你希望这份学习路线是什么样的？", 0, new Date(), true, true, false);
        chat.addTalkMsgRight("5307397", "f_11", 1, new Date(), true, true, false);
        
        chat.addTalkMsgGroupLeft("5307397", "1000002", "谢飞机-北理工", "01_50", "啊！不要大杂烩，要高质量的，我毕业后2年现在学习瓶颈了！", 0, new Date(), true, false, true);
        chat.addTalkMsgGroupLeft("5307397", "1000003", "李铁锤-大二", "03_50", "能告诉我大二到毕业都学啥吗？", 0, new Date(), true, false, true);
        chat.addTalkMsgGroupLeft("5307397", "1000004", "哈尼-大一", "04_50", "傅哥，我想知道大一学点啥。爱你！（づ￣3￣）づ╭❤～", 0, new Date(), true, false, true);
        chat.addTalkMsgGroupLeft("5307397", "1000004", "哈尼-大一", "04_50", "f_25", 1, new Date(), true, false, true);

        // 好友
        chat.addFriendUser(false, "1000004", "哈尼", "04_50");
        chat.addFriendUser(false, "1000001", "拎包冲", "02_50");
        chat.addFriendUser(false, "1000002", "铁锤", "03_50");
        chat.addFriendUser(true, "1000003", "小傅哥 | bugstack.cn", "01_50");


        ILoginMethod login = new LoginController((userId, userPassword) -> {
            System.out.println("登陆 userId：" + userId + "userPassword：" + userPassword);
        }, chat);
//        login.doShow();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
