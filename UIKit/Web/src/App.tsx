import { useState } from 'react'
import './App.css'
import AgoraUIKit from "agora-react-uikit";
// import reactLogo from './assets/react.svg'
// import viteLogo from '/vite.svg'

function App() {
  const [videoCall, setVideoCall] = useState(true);

  const rtcProps = {
      appId: "",
      channel: "",
      token: "",
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
