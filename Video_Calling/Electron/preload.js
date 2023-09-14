const 
{
    createAgoraRtcEngine,
    VideoSourceType,
    RenderModeType,
    ChannelProfileType,
    ClientRoleType
} = require("agora-electron-sdk");

// Delare the required variables.
let agoraEngine;
let localVideoContainer;
let remoteVideoContainer;

// Pass your App ID here.
const appID = "b2656396d43b4fd984f93865f98a6a2f";
// Pass the channel name you filled in to generate the temporary token.
var channel = "hennyAndroid";
// Pass your temporary token.
var token = "007eJxTYKiSScha5ff2+347yevqoUv7ntUdm+uzzd1hkxfnhlfm6ZcVGJKMzEzNjC3NUkyMk0zSUiwtTNIsjS3MTNMsLRLNEo3S1CuZUhsCGRkeHNjCwAiFID4PQ0ZqXl6lY15KUX5mCgMDAHUhIu4=";
// A variable to save the ID of the remote user.
let remoteUID;
// Local user ID.
var Uid = 0;
// Setup the required callbacks.
const EventHandles = 
{
    // Listen for the onUserJoined to setup the remote view.
    onUserJoined: (connection, remoteUid, elapsed) => 
    {
        // Save the remote UID for reuse.
        remoteUID = remoteUid;
        // Assign the remote UID to the local video container.
        remoteVideoContainer.textContent = "Remote user " + remoteUID.toString();
        // Setup remote video to display the remote video.
        agoraEngine.setupRemoteVideoEx(
        {
            sourceType: VideoSourceType.VideoSourceRemote,
            uid: remoteUid,
            view:remoteVideoContainer,
            renderMode: RenderModeType.RenderModeFit,
        },
        {
            channelId: connection.channelId
        });
    },
};
window.onload = () => 
{
    const os = require("os");
    const path = require("path");
    // Programmatically access the local video container.
    localVideoContainer = document.getElementById("join-channel-local-video");
    // Set the local container size and padding.
    localVideoContainer.style.width = "640px";
    localVideoContainer.style.height = "480px";
    localVideoContainer.style.padding = "15px 5px 5px 5px";
    // Programmatically access the remote video container.
    remoteVideoContainer = document.getElementById("join-channel-remote-video");
    // Set the remote container size and padding.
    remoteVideoContainer.style.width = "640px";
    remoteVideoContainer.style.height = "480px";
    remoteVideoContainer.style.padding = "15px 5px 5px 5px";
    // Create an agoraEngine instance.
    agoraEngine = createAgoraRtcEngine();
    // Initialize an RtcEngine instance.
    agoraEngine.initialize({appId: appID});
    // Register event handlers.
    agoraEngine.registerEventHandler(EventHandles);
    // For a video call scenario, set the channel profile as ChannelProfileCommunication.
    agoraEngine.setChannelProfile(ChannelProfileType.ChannelProfileCommunication);
    // Set the user role as Host.
    agoraEngine.setClientRole(ClientRoleType.ClientRoleBroadcaster);
    // Listen to the leave button click event.
    document.getElementById("leave").onclick = async function ()
    {
        // Stop the local preview before leaving the channel.
        agoraEngine.stopPreview();
        // Leave the channel.
        agoraEngine.leaveChannel();
        // Reload the web page for reuse.
        window.location.reload();
    }
    // Listen to the join button click event.
    document.getElementById("join").onclick = async function ()
    { 
        // Setup the local view.
        agoraEngine.setupLocalVideo(
            {
                sourceType: VideoSourceType.VideoSourceCameraPrimary,
                view: localVideoContainer,
            });

        // By default, video is disabled. You need to call enableVideo to start a video stream.
        agoraEngine.enableVideo();
        // Start local preview.
        agoraEngine.startPreview();
        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        agoraEngine.joinChannel(token, channel, Uid);        }
};
