package com.soprasteria.springboot.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.airbus.b260.b260.tools.messages.B260Messages;
import com.airbus.b260.b260.tools.messages.ComMessages;
import com.airbus.b260.b260.tools.utilities.Tools;
import com.airbus.b260.b260.tools.utilities.logger.LogMngr;

public class ExecProcess {

    /** stderr */
    private String mStderr = null;

    /**
     * stdout
     */
    private String mStdout = null;

    /**
     * return value
     */
    private int mRet = 0;

    /**
     * script command
     */
    private String mCmd = null;

    /**
     * table containing command
     */
    private String[] mTcmd = null;

    /**
     * exception string
     */
    private String mException = null;

    /**
     * Environment
     */
    private String[] mEnv = null;

    /**
     * Logger instance
     */
    protected LogMngr theLogger = LogMngr.getLogger(ExecProcess.class);

    /**
     * constructor
     * 
     * @param cmd
     *            splitted command
     */
    public ExecProcess(String[] cmd) {

        mCmd = null;
        mTcmd = cmd;
        mEnv = null;

        theLogger = LogMngr.getLogger(ExecProcess.class);

        if (theLogger.isDebugEnabled()) {
            theLogger.logDebug("runProcess run : " + cmd);
        }
    }

    /**
     * constructor
     * 
     * @justify general method
     * @param cmd
     *            command
     * @param env
     *            environnement variables
     */
    public ExecProcess(String cmd, String[] env) {

        mCmd = cmd;
        mEnv = env;

        theLogger = LogMngr.getLogger(ExecProcess.class);

        if (theLogger.isDebugEnabled()) {
            theLogger.logDebug("runProcess run : " + cmd);
        }
    }

    /**
     * constructor
     * 
     * @param cmd
     *            command
     */
    public ExecProcess(String cmd) {

        mCmd = cmd;
        mEnv = null;

        theLogger = LogMngr.getLogger(ExecProcess.class);

        if (theLogger.isDebugEnabled()) {
            theLogger.logDebug("runProcess run : " + cmd);
        }
    }

    /**
     * launch the command
     * 
     * @throws java.lang.Exception
     *             when error
     */
    public void run() throws Exception {
        run(false);
    }

    /**
     * Return the command string
     * 
     * @return command string
     */
    private String getCmdString() {

        StringBuilder s = new StringBuilder();
        if (mTcmd != null) {
            for (int k = 0; k < mTcmd.length; k++) {
                s.append(mTcmd[k]);
                s.append(" ");
            }
        } else {
            s.append(mCmd);
        }
        return s.toString();
    }

    /**
     * launch the command
     * 
     * @param allowPositifReturnCode
     *            TRUE to allow return code > 0 (For ex, cmp command can return
     *            1)
     * @throws java.lang.Exception
     *             when error
     */
    public void run(boolean allowPositifReturnCode) throws Exception {
        theLogger.logDebug("->run");

        try {
            // start process
            Process proc = null;

            // trace the command
            theLogger.logInfo(ComMessages.getString(B260Messages.I_EXEC_PROCESS_1, getCmdString()));

            // magic 3352477
            try {
                // test which constructor has been used
                if (mCmd == null) {
                    Runtime.getRuntime().gc();
                    proc = Runtime.getRuntime().exec(mTcmd, mEnv);
                } else {
                    proc = Runtime.getRuntime().exec(mCmd, mEnv);
                }
                //MF0657 Qualification infinite loop for external tool launch.
                // for JobDifferential / JobCustoDMGeneration / and b266 jobs. 
                //NO proc.waitFor() statement to declare here.
                //proc.waitFor();
                //MF0657 <--
            } catch (IOException e) {
                theLogger.logWarning(ComMessages.getString(B260Messages.W_EXEC_PROCESS_4, getCmdString()));
                throw e;
            }

            // create stream Gobbler
            StreamGobbler sgErr = new StreamGobbler(proc.getErrorStream());
            StreamGobbler sgOut = new StreamGobbler(proc.getInputStream());
            // it's normal to have getInputStream
         
            // Start these gobbler
            sgErr.start();
            sgOut.start();
           

            // wait for processes termination
            try {
                proc.waitFor();
                sgErr.join();
                sgOut.join();
             
               // Closing the streams (even if not used)
                proc.getErrorStream().close();
                proc.getInputStream().close();
                proc.getOutputStream().close();
            } catch (InterruptedException ie) {
                theLogger.logError(ComMessages.getString(B260Messages.E_EXEC_PROCESS_1));
                theLogger.logError(ie);
            }

            // update returned value
            mRet = proc.exitValue();

            // update stdout value
            mStdout = sgOut.getString();

            // update sterr value
            mStderr = sgErr.getString();

            /**
             * SKO: following workaround is obsolete for JDK 1.4+
             */
            // force garbage collector (prevents jdk 1.2 bug #4291490)
            // Runtime.getRuntime().gc();
        } catch (IOException ioe) {
            // MAGIC 3444181
            // Avoid that system error to be treated as command with non zero
            // return code
            // Do not treat such an error as bad return code
            throw ioe;
        } catch (Exception x) {
            theLogger.logError(x);
            mRet = -2;
        } 
        System.out.println("Return Code: "+mRet);
        System.out.println("Std Out: "+mStdout);
        System.out.println("Std Error: "+mStderr);
        if (mRet < 0 || (mRet > 0 && !allowPositifReturnCode)) {
            // print message + command
            StringBuilder s = new StringBuilder();
            if (mTcmd != null) {
                for (int k = 0; k < mTcmd.length; k++) {
                    s.append(mTcmd[k]);
                    s.append(" ");
                }
            } else {
                s.append(mCmd);
            }
            theLogger.logWarning(ComMessages.getString(B260Messages.W_EXEC_PROCESS_1, s.toString(), new Integer(mRet)));
            // print stdout
            if ((mStdout != null) && (mStdout.length() > 0)) {
                theLogger.logWarning(ComMessages.getString(B260Messages.W_EXEC_PROCESS_2, mStdout));
            }
            // print stderr
            if ((mStderr != null) && (mStderr.length() > 0)) {
                theLogger.logWarning(ComMessages.getString(B260Messages.W_EXEC_PROCESS_3, mStderr));
            }
            throw new ExecStatusException(ComMessages.getString(B260Messages.X_EXEC_PROCESS_4, new Integer(mRet)));
        }

        theLogger.logDebug("<-run");
    }

    /**
     * getter
     * 
     * @return return value
     */
    public int getReturnValue() {
        return mRet;
    }

    /**
     * Format path with / if sun OS or \ if windows os
     * 
     * @param path
     *            the path to be formated
     * @return the path formated
     */
    public static String formatPath(String path) {
        if (Tools.isWindowsOS()) {
            String cygPath = new String(path);
            cygPath = cygPath.replaceAll("([A-Z]):", "/cygdrive/$1");
            cygPath = cygPath.replaceAll("\\\\", "/");
            return cygPath;
        }
        return path;
    }

    /**
     * getter
     * 
     * @return stdout
     */
    public String getStdout() {
        return mStdout;
    }

    /**
     * getter
     * 
     * @return stderr
     */
    public String getStderr() {
        return mStderr;
    }

    /**
     * getter
     * 
     * @return exception string
     */
    public String getException() {
        return mException;
    }

    /**
     * this class defines an exception thrown when return value is not zero
     */
    private class ExecStatusException extends Exception {

        /**
         * Version used by the Serializable interface
         */
        static final long serialVersionUID = 0;

        /**
         * constructor
         * 
         * @param msg
         *            text of exception
         */
        public ExecStatusException(String msg) {
            super(msg);
        }
    }

    /**
     * this class prevents bug when output buffer of an exec is full
     */
    private class StreamGobbler extends Thread {

        /**
         * buffer
         */
        private StringBuilder sb;

        /**
         * input stream
         */
        private InputStream is;

        /**
         * constructor
         * 
         * @param pis
         *            input stream
         */
        public StreamGobbler(InputStream pis) {
            this.is = pis;
            this.sb = new StringBuilder();
        }

        /**
         */
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                boolean b = false;

                // flush the content of the stream in a string buffer
                while ((line = br.readLine()) != null) {
                    if (b) {
                        sb.append("\n");
                    } else {
                        b = true;
                    }
                    sb.append(line);
                }
            } catch (IOException ioe) {
                theLogger.logError(ComMessages.getString(B260Messages.E_EXEC_PROCESS_5));
                theLogger.logError(ioe);
            }
        }

        /**
         * getter
         * 
         * @return string buffer
         */
        public String getString() {
            return sb.toString();
        }
    }
    
    public static void main(String[] args) throws Exception {
    	ExecProcess p = null;
    	String cmd = "";

        try {
        	cmd = "bash /home/user/test.sh -help"; 
            p = new ExecProcess(cmd);
            p.run();

        } catch (Exception e) {
            
            p.theLogger.logError("Error during execution of " + cmd);
            throw e;
            
        } 
	}
}
