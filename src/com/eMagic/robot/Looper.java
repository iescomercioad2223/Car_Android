/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eMagic.robot;


import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
    public class Looper extends BaseIOIOLooper {

    private DigitalOutput pin1, pin2, pin4, pin5, pin6, pin7;
    private byte datosHiloIOIO;
    private boolean seguir;
    

    public Looper() {
        seguir = true;
        datosHiloIOIO = 0;
    }
       
    /**
     * Called every time a connection with IOIO has been established. Typically
     * used to open pins.
     *
     * @throws ConnectionLostException When IOIO connection is lost.
     *
     * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
     */
    @Override
    protected void setup() throws ConnectionLostException {    
        try { 
//            ioio = IOIOFactory.create();
//            ioio.waitForConnect();            
            pin1 = ioio_.openDigitalOutput(1, false); 
            pin2 = ioio_.openDigitalOutput(2, false); 
            pin4 = ioio_.openDigitalOutput(4, false); 
            pin5 = ioio_.openDigitalOutput(5, false); 
            pin6 = ioio_.openDigitalOutput(6, false); 
            pin7 = ioio_.openDigitalOutput(7, false); 
        } catch (ConnectionLostException ex) {
            Logger.getLogger(Android_Activity.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }

    /**
     * Called repetitively while the IOIO is connected.
     *
     * @throws ConnectionLostException When IOIO connection is lost.
     *
     * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
     */
    @Override
    public void loop() throws ConnectionLostException {   
        while (seguir) {            
            if ((datosHiloIOIO & 127)!= 127)  {
                try {
                    // Izquierda, Derecha, Adelante, Atras, Luz y Bocina
                    if ((datosHiloIOIO & 80)== 80)  // Pulsado derecha
                        giroDerechaRapido();
                    else  if ((datosHiloIOIO & 40) == 40)  // Giro a la izquierda
                        giroIzquierdaRapido();
                    else if  ((datosHiloIOIO & 72) == 72)// Adelante
                        avanzar();
                    else if ((datosHiloIOIO & 48) == 48) // Atras
                        retroceder();
                    else if ((datosHiloIOIO & 64) == 64) // Diagonal izquierda arriba
                        diagonalLeftUp();
                    else if ((datosHiloIOIO & 32) == 32) // Diagonal derecha arriba
                        diagonalRightUp();
                    else if ((datosHiloIOIO & 16) == 16) // Diagonal abajo izquierda
                        diagonalLeftDown();
                    else if ((datosHiloIOIO & 8) == 8) // Diagonal abajo derecha
                        diagonalRightDown();
                    else // default Dirección desactivada
                        stopTraccion();                  
                    
                    if ((datosHiloIOIO & 1) == 1) // Activación Luz
                        luzOnOff(true);
                    else
                        luzOnOff(false);
                    
                    if ((datosHiloIOIO & 2) == 2)  // Bocina
                        bocinaOnOff(true);
                    else
                        bocinaOnOff(false); // Desactivar Bocina                                
                } catch (InterruptedException ex) {
                    Logger.getLogger(Looper.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                seguir = !seguir; // Detener ciclo                
            }            
        }        
    }
    
    
    /* ****************************************************** */
    /* Comandos para el robot
    /* ****************************************************** */
     public void stopTraccion() throws ConnectionLostException{
        pin4.write(false);
        pin5.write(false);
        pin6.write(false);
        pin7.write(false);			
    }

    public void avanzar() throws ConnectionLostException, InterruptedException {
        pin4.write(true);
        pin7.write(true);       	
    }		
    public void retroceder() throws ConnectionLostException, InterruptedException {
        pin5.write(true);
        pin6.write(true);
//        Thread.sleep(atime);
//        stopTraccion();			
    }		
    public void giroIzquierdaLento() throws ConnectionLostException, InterruptedException {
        // Avanza solo la rueda derecha
        pin4.write(true);
//        Thread.sleep(atime);
//        stopTraccion();				
    }
    public void giroDerechaLento() throws ConnectionLostException, InterruptedException {
        // Avanza sólo la rueda izquierda
        pin7.write(true);
//        Thread.sleep(atime);
//        stopTraccion();	;			
    }
    public void giroIzquierdaRapido() throws ConnectionLostException, InterruptedException {
        // Avanza rueda derecha y retrocede rueda izquierda
        pin6.write(true);
        pin4.write(true);
//        Thread.sleep(atime);
//        stopTraccion();				
    }
    public void giroDerechaRapido() throws ConnectionLostException, InterruptedException {
        // Avanza rueda izquierda y retrocede rueda derecha
        pin5.write(true);
        pin7.write(true);
//        Thread.sleep(atime);
//        stopTraccion();				
    }
    
    public void luzOnOff(boolean aux) throws ConnectionLostException {
        // Enciende Luz
        if (aux) pin1.write(true); else pin1.write(false);
    }

    public void bocinaOnOff(boolean aux) throws ConnectionLostException {
        // Enciende bocina
        if (aux) pin2.write(true); else pin2.write(false);
    }
    
    public void setDatosHiloIOIO(byte aux) {
        datosHiloIOIO = aux;
    }

    public void stop() {
        seguir = false;
    }

    private void diagonalRightUp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void diagonalLeftDown() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void diagonalRightDown() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void diagonalLeftUp() {
        try {
            // Avanza rueda derecha y pulsos sobre rueda izquierda
            pin6.write(true);
            Thread.sleep(25);
            pin5.write(true);
            Thread.sleep(25);
            pin5.write(false);
        } catch (ConnectionLostException ex) {
            Logger.getLogger(Looper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Looper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    }
