package com.soprasteria.springboot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.soprasteria.springboot.constants.Messages;

/**
 * Class for launching an UNIX (Windows) command
 */

public class ExecProcess {

    /** Standard Error */
    private String mStderr = null;

    /**
     * Standard Output
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
     * Logger
     */
    private Logger theLoggger = Logger.getLogger(getClass().getName());

    /**
     * constructor
     * @param cmd command
     */
    public ExecProcess(String[] cmd) {
        mCmd = null;
        mTcmd = cmd;
        mEnv = null;
    }

    /**
     * constructor
     * 
     * @justify general method
     * @param cmd
     *            command
     * @param env
     *            Environment variables
     */
    public ExecProcess(String cmd, String[] env) {
        mCmd = cmd;
        mEnv = env;
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
    }

    /**
     * launch the command
     * @throws IOException, ExecStatusException
     *             when error
     */
    public void run() throws IOException, ExecStatusException {
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
     * @param allowPositifReturnCode
     *            TRUE to allow return code > 0 (For example, copy command can return 1)
     * @throws java.lang.Exception
     *             when error
     */
    public void run(boolean allowPositifReturnCode) throws IOException, ExecStatusException {
        try {
            // trace the command
            theLoggger.log(Level.INFO, getCmdString());
            
            // start process
            Process proc = getRunTime();

            // create stream Gobbler
            StreamGobbler sgErr = new StreamGobbler(proc.getErrorStream());
            StreamGobbler sgOut = new StreamGobbler(proc.getInputStream());
            // it's normal to have getInputStream
         
            // Start these gobbler
            sgErr.start();
            sgOut.start();

            // wait for processes termination
            process(proc, sgErr, sgOut);

            // update returned value
            mRet = proc.exitValue();

            // update Standard Output
            mStdout = sgOut.getString();

            // update Standard Error
            mStderr = sgErr.getString();
            
            // trace the Exit Codes
            theLoggger.log(Level.INFO, Messages.RETURN_VALUE, mRet);
            theLoggger.log(Level.INFO, Messages.OUTPUT, mStdout);
            theLoggger.log(Level.INFO, Messages.ERROR, mStderr);
        } catch (IOException ioe) {
            // Avoid that system error to be treated as command with non zero return code
            // Do not treat such an error as bad return code
            throw ioe;
        } catch (Exception x) {
            theLoggger.log(Level.SEVERE, x.getMessage());
            mRet = -2;
        } 

        execStatus(allowPositifReturnCode);
    }

	/**
	 * @param allowPositifReturnCode
	 * @throws ExecStatusException
	 */
	private void execStatus(boolean allowPositifReturnCode) throws ExecStatusException {
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
            
            theLoggger.log(Level.SEVERE, Messages.SOMETHING_WRONG_IN_COMMAND, s.toString());
            theLoggger.log(Level.SEVERE, Messages.RETURN_VALUE, mRet);
            
            // print Standard Output
            if ((mStdout != null) && (mStdout.length() > 0)) {
            	theLoggger.log(Level.SEVERE, Messages.OUTPUT, mStdout);
            }
            
            // print Standard Error
            if ((mStderr != null) && (mStderr.length() > 0)) {
            	theLoggger.log(Level.SEVERE, Messages.ERROR, mStderr);
            }
            throw new ExecStatusException(Messages.SOMETHING_WRONG_IN_COMMAND + s.toString() + Messages.DETAILS   + getStdout());
        }
	}

	/**
	 * @param proc current run time
	 * @param sgErr Stream for Error
	 * @param sgOut Stream for Output
	 * @throws IOException 
	 * @throws InterruptedException
	 */
	private void process(Process proc, StreamGobbler sgErr, StreamGobbler sgOut)
			throws IOException, InterruptedException {
		try {
		    proc.waitFor();
		    sgErr.join();
		    sgOut.join();
		 
		   // Closing the streams (even if not used)
		    proc.getErrorStream().close();
		    proc.getInputStream().close();
		    proc.getOutputStream().close();
		} catch (InterruptedException ie) {
		    theLoggger.log(Level.SEVERE, Messages.ERROR_WHILE_WAITING);
		    throw ie;
		}
	}

	/**
	 * @return the Runtime according to Operating System
	 * @throws IOException
	 */
	private Process getRunTime() throws IOException {
		Process process = null;
		try {
		    // test which constructor has been used
		    if (mCmd == null) {
		        process = Runtime.getRuntime().exec(mTcmd, mEnv);
		    } else {
		    	process = Runtime.getRuntime().exec(mCmd, mEnv);
		    }
		} catch (IOException e) {
			theLoggger.log(Level.SEVERE, Messages.SOMETHING_WRONG_IN_COMMAND, getCmdString());
		    throw e;
		}
		return process;
	}

    /**
     * getter
     * @return return value
     */
    public int getReturnValue() {
        return mRet;
    }

    /**
     * getter
     * 
     * @return Standard Output
     */
    public String getStdout() {
        return mStdout;
    }

    /**
     * getter
     * 
     * @return Standard Error
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
        @Override
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
            	theLoggger.log(Level.SEVERE, Messages.ERROR_WHILE_READING_EXECUTION);
            	theLoggger.log(Level.SEVERE, ioe.getMessage());
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

}
