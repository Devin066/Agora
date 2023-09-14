import { useState } from 'react'
import './App.css'
import AgoraUIKit from "agora-react-uikit";
// import reactLogo from './assets/react.svg'
// import viteLogo from '/vite.svg'

function App() {
  const [videoCall, setVideoCall] = useState(true);

  const rtcProps = {
      appId: "b2656396d43b4fd984f93865f98a6a2f",
      channel: "hennyAndroid",
      token: "007eJxTYKiSScha5ff2+347yevqoUv7ntUdm+uzzd1hkxfnhlfm6ZcVGJKMzEzNjC3NUkyMk0zSUiwtTNIsjS3MTNMsLRLNEo3S1CuZUhsCGRkeHNjCwAiFID4PQ0ZqXl6lY15KUX5mCgMDAHUhIu4=",
  };
  const callbacks = {
    EndCall: () => setVideoCall(false),
  };
  return videoCall ? (
    <div style={{ display: "flex", width: "1280px", height: "720px" }}>
      <AgoraUIKit rtcProps={rtcProps} callbacks={callbacks} />
    </div>
  ) : (
    <h3 onClick={() => setVideoCall(true)}>Join</h3>
  );
}

export default App
