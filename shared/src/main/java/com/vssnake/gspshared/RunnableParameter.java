package com.vssnake.gspshared;

/**
 * Created by vssnake on 09/01/2015.
 */
public abstract class RunnableParameter<T> implements Runnable {
    T parameter;
    public T getParameter(){ return parameter;}
    public void setParameter(T parameter){ this.parameter = parameter;}
    //OneShotTask(String s) { str = s; }
        /*public void run() {
            someFunc(str);
        }*/
}