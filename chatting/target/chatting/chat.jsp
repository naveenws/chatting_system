<%@ page session="true" %>

<%
    String username = (String) session.getAttribute("username");

    if (username == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>

<head>

    <title>Simple Chatting System</title>

    <style>

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f2f2f2;
        }

        .chat-container {
            width: 600px;
            margin: 40px auto;
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 0 10px #aaa;
        }

        h2 {
            text-align: center;
        }

        .user-info {
            margin-bottom: 15px;
        }

        input {
            width: 100%;
            padding: 12px;
            margin: 8px 0;
            box-sizing: border-box;
        }

        button {
            padding: 12px 20px;
            background: #333;
            color: white;
            border: none;
            cursor: pointer;
        }

        #messages {
            height: 350px;
            overflow-y: auto;
            border: 1px solid #ccc;
            padding: 10px;
            margin: 15px 0;
            background: #fafafa;
        }

        .message {
            padding: 8px;
            margin: 8px 0;
            border-bottom: 1px solid #ddd;
        }

        .time {
            font-size: 11px;
            color: gray;
        }

    </style>

</head>

<body>

<div class="chat-container">

    <h2>Simple Chatting System</h2>

    <div class="user-info">
        Logged in as:
        <strong><%= username %></strong>
    </div>

    <input type="text"
           id="receiver"
           placeholder="Enter receiver username"
           required>

    <div id="messages">
        Select a user to start chatting.
    </div>

    <input type="text"
           id="message"
           placeholder="Type your message">

    <button onclick="sendMessage()">
        Send
    </button>

</div>


<script>

function sendMessage() {

    let receiver =
        document.getElementById("receiver").value;

    let message =
        document.getElementById("message").value;

    if (receiver === "" || message === "") {

        alert("Please enter receiver and message");

        return;
    }

    let xhr = new XMLHttpRequest();

    xhr.open("POST", "ChatServlet", true);

    xhr.setRequestHeader(
        "Content-Type",
        "application/x-www-form-urlencoded"
    );

    xhr.onload = function() {

        if (xhr.status === 200) {

            document.getElementById("message").value = "";

            loadMessages();
        }
    };

    xhr.send(
        "action=send"
        + "&receiver=" + encodeURIComponent(receiver)
        + "&message=" + encodeURIComponent(message)
    );
}


function loadMessages() {

    let receiver =
        document.getElementById("receiver").value;

    if (receiver === "") {
        return;
    }

    let xhr = new XMLHttpRequest();

    xhr.open(
        "GET",
        "ChatServlet?action=get&receiver="
        + encodeURIComponent(receiver),
        true
    );

    xhr.onload = function() {

        if (xhr.status === 200) {

            document.getElementById("messages").innerHTML =
                xhr.responseText;
        }
    };

    xhr.send();
}

</script>

</body>

</html>