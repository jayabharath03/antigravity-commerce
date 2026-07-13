import { Client, type IMessage } from '@stomp/stompjs';

const WS_URL =
  (import.meta.env.VITE_WS_URL as string | undefined) || 'ws://localhost:8080/ws';

type Subscription = {
  topic: string;
  cb: (body: any) => void;
  handle?: { unsubscribe: () => void };
};

const subscriptions: Subscription[] = [];
let client: Client | null = null;

function ensureClient(): Client {
  if (client) return client;
  client = new Client({
    brokerURL: WS_URL,
    reconnectDelay: 5000,
    onConnect: () => {
      // (Re)subscribe everything after a connect or reconnect.
      subscriptions.forEach((s) => {
        s.handle = client!.subscribe(s.topic, (m: IMessage) => s.cb(JSON.parse(m.body)));
      });
    },
  });
  client.activate();
  return client;
}

/**
 * Subscribe to a STOMP topic. Returns an unsubscribe function.
 * Safe to call before the socket is connected — it will subscribe on connect.
 */
export function subscribe(topic: string, cb: (body: any) => void): () => void {
  const sub: Subscription = { topic, cb };
  subscriptions.push(sub);

  const c = ensureClient();
  if (c.connected) {
    sub.handle = c.subscribe(topic, (m: IMessage) => cb(JSON.parse(m.body)));
  }

  return () => {
    sub.handle?.unsubscribe();
    const i = subscriptions.indexOf(sub);
    if (i >= 0) subscriptions.splice(i, 1);
  };
}
