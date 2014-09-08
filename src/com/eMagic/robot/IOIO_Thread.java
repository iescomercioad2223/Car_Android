/*******************************************************************************************************
/* NO USADO HASTA EL MOMENTO 
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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOIO_Thread extends Thread {
    private DatagramSocket socket;  
    private Looper loop;
    private Android_Activity gui;
    private String IP_Adress;
    private boolean abort_;
    private byte datosHiloIOIO;
    private InetAddress serverAddr;
    private int size_p;


    public IOIO_Thread(Android_Activity the_app, String ip_address, Looper aloop) {       
        loop = aloop;
        gui = the_app;
        IP_Adress = ip_address;
        envioIPMovil();
    }
    
    /* ****************************************************** */
    /* Envio inicial de la IP del movil al servidor-PC
    /* ****************************************************** */
     private void envioIPMovil() {
               /****** Constructor ****/
            try {                
                serverAddr = InetAddress.getByName(IP_Adress); 
                socket = new DatagramSocket(54322);                       
                byte[] data = InetAddress.getLocalHost().getAddress(); // Mi ip          
                size_p = data.length;
                DatagramPacket packet = new DatagramPacket(data, size_p, serverAddr, 6791);                
                socket.send(packet); // Envio dirección IP al servidor para que sepa IP del movil
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(Android_Activity.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SocketException ex) {
                Logger.getLogger(Android_Activity.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) { 
                Logger.getLogger(Android_Activity.class.getName()).log(Level.SEVERE, null, ex);
            }    
        }
    
       
    @Override
    public void run()  { 
        DatagramPacket packet;        
        byte[] data = new byte[1];
        int size_p = data.length;
        
        while (true) {             
            try {             
                packet = new DatagramPacket(data, size_p);
                socket.receive(packet); // Recibimos movimiento seleccionado por el usuario en GUI servidor          
                data[0] = packet.getData()[0];                
                loop.setDatosHiloIOIO(data[0]);               
                
            } catch (IOException ex) {
                Logger.getLogger(IOIO_Thread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
              
    }
   
                    
/** Not relevant to subclasses. */
    public synchronized final void abort() 	{
        abort_ = true;
        loop.stop();
        socket.close();
        
    }

    
    
}