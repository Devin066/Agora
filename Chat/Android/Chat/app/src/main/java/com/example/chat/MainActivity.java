package com.example.chat;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import android.graphics.Color;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.util.Log;
import java.util.List;

import io.agora.CallBack;
import io.agora.ConnectionListener;
import io.agora.MessageListener;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatOptions;
import io.agora.chat.TextMessageBody;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private String userId = "henny";
    private String token = "007eJxTYPgTZ38+urtt6hcuYx+zfoWZoRvmyrL2nb3vWz7z4zOxfXMVGJINTcxNLMzNjA3NU0xS04wtDVPNU5LNTAwMUtNMDY2SK+bJpTYEMjI4OB5kZmRgZWAEQhBfhSEtxdjQMNnMQNfMKCVV19AwNVXXMtHCSNfA2NzQIjHVLMXS0hwAWNImpQ==";
    private String appKey = "611028380#1212919";
    private ChatClient agoraChatClient;
    private boolean isJoined = false;
    EditText editMessage;

    private void showLog(String text) {
        // Show a toast message
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show());

        // Write log
        Log.d("AgoraChatQuickStart", text);
    }
    private void setupChatClient() {
        ChatOptions options = new ChatOptions();
        if (appKey.isEmpty()) {
            showLog("You need to set your AppKey");
            return;
        }
        options.setAppKey(appKey); // Set your app key in options
        agoraChatClient = ChatClient.getInstance();
        agoraChatClient.init(this, options); // Initialize the ChatClient
        agoraChatClient.setDebugMode(true); // Enable debug info output
    }
    private void setupListeners() {
        // Add message event callbacks
        agoraChatClient.chatManager().addMessageListener(new MessageListener() {
            @Override
            public void onMessageReceived(List<ChatMessage> messages) {
                for (ChatMessage message : messages) {
                    runOnUiThread(() ->
                            displayMessage(((TextMessageBody) message.getBody()).getMessage(),
                                    false)
                    );
                    showLog("Received a " + message.getType().name()
                            + " message from " + message.getFrom());
                }
            }
        });

        // Add connection event callbacks
        agoraChatClient.addConnectionListener(new ConnectionListener() {
            @Override
            public void onConnected() {
                showLog("Connected");
            }

            @Override
            public void onDisconnected(int error) {
                if (isJoined) {
                    showLog("Disconnected: " + error);
                    isJoined = false;
                }
            }

            @Override
            public void onLogout(int errorCode) {
                showLog("User logging out: " + errorCode);
            }

            @Override
            public void onTokenExpired() {
                // The token has expired
            }

            @Override
            public void onTokenWillExpire() {
                // The token is about to expire. Get a new token
                // from the token server and renew the token.
            }
        });
    }
    public void joinLeave(View view) {
        Button button = findViewById(R.id.btnJoinLeave);

        if (isJoined) {
            agoraChatClient.logout(true, new CallBack() {
                @Override
                public void onSuccess() {
                    showLog("Sign out success!");
                    runOnUiThread(() -> button.setText("Join"));
                    isJoined = false;
                }
                @Override
                public void onError(int code, String error) {
                    showLog(error);
                }
            });
        } else {
            agoraChatClient.loginWithAgoraToken(userId, token, new CallBack() {
                @Override
                public void onSuccess() {
                    showLog("Signed in");
                    isJoined = true;
                    runOnUiThread(() -> button.setText("Leave"));
                }
                @Override
                public void onError(int code, String error) {
                    if (code == 200) { // Already joined
                        isJoined = true;
                        runOnUiThread(() -> button.setText("Leave"));
                    } else {
                        showLog(error);
                    }
                }
            });
        }
    }
    public void sendMessage(View view) {
        // Read the recipient name from the EditText box
        String toSendName = ((EditText) findViewById(R.id.etRecipient)).getText().toString().trim();
        String content = editMessage.getText().toString().trim();

        if (toSendName.isEmpty() || content.isEmpty()) {
            showLog("Enter a recipient name and a message");
            return;
        }

        // Create a ChatMessage
        ChatMessage message = ChatMessage.createTextSendMessage(content, toSendName);

        // Set the message callback before sending the message
        message.setMessageStatusCallback(new CallBack() {
            @Override
            public void onSuccess() {
                showLog("Message sent");
                runOnUiThread(() -> {
                    displayMessage(content, true);
                    // Clear the box and hide the keyboard after sending the message
                    editMessage.setText("");
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(editMessage.getApplicationWindowToken(),0);
                });
            }

            @Override
            public void onError(int code, String error) {
                showLog(error);
            }
        });

        // Send the message
        agoraChatClient.chatManager().sendMessage(message);
    }
    void displayMessage(String messageText, boolean isSentMessage) {
        // Create a new TextView
        final TextView messageTextView = new TextView(this);
        messageTextView.setText(messageText);
        messageTextView.setPadding(10,10,10,10);

        // Set formatting
        LinearLayout messageList = findViewById(R.id.messageList);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        if (isSentMessage) {
            params.gravity = Gravity.END;
            messageTextView.setBackgroundColor(Color.parseColor("#DCF8C6"));
            params.setMargins(100,25,15,5);
        } else {
            messageTextView.setBackgroundColor(Color.parseColor("white"));
            params.setMargins(15,25,100,5);
        }

        // Add the message TextView to the LinearLayout
        messageList.addView(messageTextView, params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupChatClient(); // Initialize the ChatClient
        setupListeners(); // Add event listeners

        // Set up UI elements for code access
        editMessage = findViewById(R.id.etMessageText);
    }

}