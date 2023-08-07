import websocket
import threading

def on_message(ws, message):
    print("Received:", message)

def on_error(ws, error):
    print("Error:", error)

def on_close(ws, close_status_code, close_msg):
    print("Closed:", close_msg)

def on_open(ws):
    print("Connected to server.")

    def send_message():
        while True:
            user_input = input("Enter a message to send (or 'exit' to quit): ")
            if user_input == 'exit':
                ws.close()
                break
            ws.send(user_input)

    threading.Thread(target=send_message).start()

if __name__ == "__main__":
    port_number = "5789"
    server_url = f"ws://127.0.0.1:{port_number}/WS2Applet"

    ws = websocket.WebSocketApp(server_url,
                                on_message=on_message,
                                on_error=on_error,
                                on_close=on_close)
    ws.on_open = on_open

    ws.run_forever()
