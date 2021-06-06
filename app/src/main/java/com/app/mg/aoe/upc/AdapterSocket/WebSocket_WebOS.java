//package com.app.mg.aoe.upc.AdapterSocket;



/*public class WebSocket_WebOS extends Activity {
    private TextView output;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = (TextView) findViewById(R.id.TextView01);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitNetwork().build());
        ejecutaCliente();
    }
    private void ejecutaCliente() {
        String ip = "158.42.146.127";
        int puerto = 7;
        log(" socket " + ip + " " + puerto);
        try {
            Socket sk = new Socket(ip, puerto);
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(sk.getInputStream()));
            PrintWriter salida = new PrintWriter(
                    new OutputStreamWriter(sk.getOutputStream()), true);
            log("enviando...");
            salida.println("ip");
            log("recibiendo ... " + entrada.readLine());
            sk.close();
        } catch (Exception e) {
            log("error: " + e.toString());
        }
    }
    private void log(String string) {
        output.append(string + "\n");
    }
}

*/