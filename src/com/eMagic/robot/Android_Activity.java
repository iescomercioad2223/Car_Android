/*******************************************************************************************************
Copyright (c) 2011 Regents of the University of California.
All rights reserved.

This software was developed at the University of California, Irvine.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in
   the documentation and/or other materials provided with the
   distribution.

3. All advertising materials mentioning features or use of this
   software must display the following acknowledgment:
   "This product includes software developed at the University of
   California, Irvine by Nicolas Oros, Ph.D.
   (http://www.cogsci.uci.edu/~noros/)."

4. The name of the University may not be used to endorse or promote
   products derived from this software without specific prior written
   permission.

5. Redistributions of any form whatsoever must retain the following
   acknowledgment:
   "This product includes software developed at the University of
   California, Irvine by Nicolas Oros, Ph.D.
   (http://www.cogsci.uci.edu/~noros/)."

THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
IN NO EVENT SHALL THE UNIVERSITY OR THE PROGRAM CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/
package com.eMagic.robot;

import static android.content.Context.SENSOR_SERVICE;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import ioio.lib.api.IOIO;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import java.net.DatagramSocket;
import java.net.InetAddress;

// ----------------------------------------------------------------------

public class Android_Activity extends IOIOActivity {   	
    
    ToggleButton togglebuttonIOIO, togglebuttonCamera, togglebuttonSensor;  
    private EditText ip_text;
    private TextView txtView;
    SensorManager sm = null;
    SurfaceView view;    
    String IPAddressServer;
    Android_Activity the_app;    
    
    private int size_p;     
    public IOIO_Thread hiloIOIO; 
    public Cam_Thread hiloCamera;
    public Sensors_Thread hiloSensors;
    protected IOIO ioio;
    private Looper loop;
    private InetAddress serverAddr;
    private DatagramSocket socket;
 
     
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        view = new SurfaceView (this);		
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);        
        ip_text = (EditText) findViewById(R.id.IP_edit_txt);
        IPAddressServer = ip_text.getText().toString();
        txtView = (TextView) findViewById(R.id.textView1);          
        the_app = this;        
        togglebuttonSensor = (ToggleButton) findViewById(R.id.SensorButton); 
        togglebuttonCamera = (ToggleButton) findViewById(R.id.CameraButton); 
        togglebuttonIOIO = (ToggleButton) findViewById(R.id.IOIOButton); 
        
        // Envio de mi IP al servidor 
        Toast.makeText(Android_Activity.this, "Send IP to Server...", Toast.LENGTH_SHORT).show();         
        hiloIOIO = null;
        hiloCamera = null;
        hiloSensors = null;
        
        // ***********************************************************************        
        togglebuttonIOIO.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {                
                // *********************************************************************** 
                // Manejo de IOIO
                // *********************************************************************** 
                if (togglebuttonIOIO.isChecked()) {
                    if (hiloIOIO==null) {
                        // Añadido la linea siguiente en método createIOIOLooper para envio IP nada mas arrancar
                        
                        hiloIOIO = new IOIO_Thread(the_app, IPAddressServer, loop);
                        hiloIOIO.start();
                     } 
                } else {
                        loop.setDatosHiloIOIO((byte)0);
                        hiloIOIO.abort();
                        Toast.makeText(Android_Activity.this, "Stop Leasing IOIO", Toast.LENGTH_SHORT).show();
                    }
                }            
        });
        
        togglebuttonSensor.setOnClickListener(new View.OnClickListener() {   
            public void onClick(View view2) {                 
                 if (togglebuttonSensor.isChecked()) {                                        
                    if (hiloSensors == null) {
                        hiloSensors = new Sensors_Thread(sm, IPAddressServer); // Arranca hilo maneja sensores                                             
                        sm.registerListener(hiloSensors, 
                        SensorManager.SENSOR_ORIENTATION |SensorManager.SENSOR_ACCELEROMETER,
                        SensorManager.SENSOR_DELAY_UI);                        
                    }                          
                 } else {
                    hiloSensors.stop_thread();
                    sm.unregisterListener(hiloSensors);
                    Toast.makeText(Android_Activity.this, "Stop streaming Sensors", Toast.LENGTH_SHORT).show();
                 }
            }                        
            });     
        togglebuttonCamera.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                 if (togglebuttonCamera.isChecked()) {                                        
                    if (hiloCamera == null) {
                        hiloCamera = new Cam_Thread (view,IPAddressServer);
                        hiloCamera.start_thread();                        
                    } 
                } else {
                     hiloCamera.stop_thread();
                     Toast.makeText(Android_Activity.this, "Stop streaming Camera", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() 
    {
        super.onResume();      
    }

    @Override
    protected void onStop()
    {
        super.onStop();       
        hiloCamera.stop_thread();
        hiloIOIO.abort();
        hiloSensors.stop_thread();
        sm.unregisterListener(hiloSensors);
        Toast.makeText(Android_Activity.this, "Stop streaming", Toast.LENGTH_SHORT).show();
        this.finish();
    }

       
    
    @Override
    protected IOIOLooper createIOIOLooper() { 
        loop = new Looper();        
        return loop;
    }
    
}
        

