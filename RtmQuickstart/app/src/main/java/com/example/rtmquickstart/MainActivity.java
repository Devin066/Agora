package com.example.rtmquickstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.SendMessageOptions;

public class MainActivity extends AppCompatActivity {

// Define global variables

    // EditText objects from the UI
    private EditText et_uid;
    private EditText et_channel_name;
    private EditText et_message_content;
    private EditText et_peer_id;

    // <Vg k="MESS" /> uid
    private String uid;
    // <Vg k="MESS" /> channel name
    private String channel_name;
    // Agora App ID
    private String AppID;

    // <Vg k="MESS" /> client instance
    private RtmClient mRtmClient;
    // <Vg k="MESS" /> channel instance
    private RtmChannel mRtmChannel;

    // TextView to show message records in the UI
    private TextView message_history;

    // <Vg k="MESS" /> user ID of the message receiver
    private String peer_id;
    // Message content
    private String message_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the <Vg k="MESS" /> client
        try {
            AppID = getBaseContext().getString(R.string.app_id);
            // Initialize the <Vg k="MESS" /> client
            mRtmClient = RtmClient.createInstance(getBaseContext(), AppID,
                    new RtmClientListener() {
                        @Override
                        public void onConnectionStateChanged(int state, int reason) {
                            String text = "Connection state changed to " + state + "Reason: " + reason + "\n";
                            writeToMessageHistory(text);
                        }

                        @Override
                        public void onImageMessageReceivedFromPeer(RtmImageMessage rtmImageMessage, String s) {
                        }

                        @Override
                        public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {
                        }

                        @Override
                        public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {
                        }

                        @Override
                        public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {
                        }

                        @Override
                        public void onTokenExpired() {
                        }

                        @Override
                        public void onPeersOnlineStatusChanged(Map<String, Integer> map) {
                        }

                        @Override
                        public void onMessageReceived(RtmMessage rtmMessage, String peerId) {
                            String text = "Message received from " + peerId + " Message: " + rtmMessage.getText() + "\n";
                            writeToMessageHistory(text);
                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException("<Vg k=\"MESS\" /> initialization failed!");
        }
    }

    // Button to login to Signaling
    public void onClickLogin(View v)
    {
        et_uid = (EditText) findViewById(R.id.uid);
        uid = et_uid.getText().toString();

        String token =getBaseContext().getString(R.string.token);

        // Log in to Signaling
        mRtmClient.login(token, uid, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                CharSequence text = "User: " + uid + " failed to log in to Signaling!" + errorInfo.toString();
                int duration = Toast.LENGTH_SHORT;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                        toast.show();
                    }
                });

            }
        });
    }

    // Button to join the <Vg k="MESS" /> channel
    public void onClickJoin(View v)
    {
        et_channel_name = (EditText) findViewById(R.id.channel_name);
        channel_name = et_channel_name.getText().toString();
        // Create a channel listener
        RtmChannelListener mRtmChannelListener = new RtmChannelListener() {
            @Override
            public void onMemberCountUpdated(int i) {

            }

            @Override
            public void onAttributesUpdated(List<RtmChannelAttribute> list) {

            }

            @Override
            public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {
                String text = message.getText();
                String fromUser = fromMember.getUserId();

                String message_text = "Message received from " + fromUser + " : " + text + "\n";
                writeToMessageHistory(message_text);

            }

            @Override
            public void onImageMessageReceived(RtmImageMessage rtmImageMessage, RtmChannelMember rtmChannelMember) {

            }

            @Override
            public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {

            }

            @Override
            public void onMemberJoined(RtmChannelMember member) {

            }

            @Override
            public void onMemberLeft(RtmChannelMember member) {

            }
        };

        try {
            // Create an <Vg k="MESS" /> channel
            mRtmChannel = mRtmClient.createChannel(channel_name, mRtmChannelListener);
        } catch (RuntimeException e) {
        }
        // Join the <Vg k="MESS" /> channel
        mRtmChannel.join(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                CharSequence text = "User: " + uid + " failed to join the channel!" + errorInfo.toString();
                int duration = Toast.LENGTH_SHORT;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                        toast.show();
                    }
                });

            }
        });

    }

    // Button to log out of Signaling
    public void onClickLogout(View v)
    {
        // Log out of Signaling
        mRtmClient.logout(null);
    }

    // Button to leave the <Vg k="MESS" /> channel
    public void onClickLeave(View v)
    {
        // Leave the <Vg k="MESS" /> channel
        mRtmChannel.leave(null);
    }
    // Button to send peer-to-peer message
    public void onClickSendPeerMsg(View v)
    {
        et_message_content = findViewById(R.id.msg_box);
        message_content = et_message_content.getText().toString();

        et_peer_id = findViewById(R.id.peer_name);
        peer_id = et_peer_id.getText().toString();

        // Create <Vg k="MESS" /> message instance
        final RtmMessage message = mRtmClient.createMessage();
        message.setText(message_content);

        SendMessageOptions option = new SendMessageOptions();
        option.enableOfflineMessaging = true;

        // Send message to peer
        mRtmClient.sendMessageToPeer(peer_id, message, option, new ResultCallback<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                String text = "Message sent from " + uid + " To " + peer_id + " ： " + message.getText() + "\n";
                writeToMessageHistory(text);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                String text = "Message fails to send from " + uid + " To " + peer_id + " Error ： " + errorInfo + "\n";
                writeToMessageHistory(text);

            }
        });

    }
    // Button to send channel message
    public void onClickSendChannelMsg(View v)
    {
        et_message_content = findViewById(R.id.msg_box);
        message_content = et_message_content.getText().toString();

        // Create <Vg k="MESS" /> message instance
        RtmMessage message = mRtmClient.createMessage();
        message.setText(message_content);

        // Send message to channel
        mRtmChannel.sendMessage(message, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String text = "Message sent to channel " + mRtmChannel.getId() + " : " + message.getText() + "\n";
                writeToMessageHistory(text);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                String text = "Message fails to send to channel " + mRtmChannel.getId() + " Error: " + errorInfo + "\n";
                writeToMessageHistory(text);
            }
        });

    }

    // Write message records to the TextView
    public void writeToMessageHistory(String record)
    {
        message_history = findViewById(R.id.message_history);
        message_history.append(record);
    }

}
