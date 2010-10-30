/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * <http://www.gnu.org/licenses/>.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Matthew J. Dovey - initial API and implementation
 ******************************************************************************/
package com.ceridwen.circulation.SIP.transport;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>

 * @author Matthew J. Dovey
 * @version 1.0
 */

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ceridwen.circulation.SIP.exceptions.ChecksumError;
import com.ceridwen.circulation.SIP.exceptions.ConnectionFailure;
import com.ceridwen.circulation.SIP.exceptions.MandatoryFieldOmitted;
import com.ceridwen.circulation.SIP.exceptions.RetriesExceeded;
import com.ceridwen.circulation.SIP.exceptions.SequenceError;
import com.ceridwen.circulation.SIP.messages.Message;
import com.ceridwen.circulation.SIP.messages.SCResend;
import com.ceridwen.circulation.SIP.exceptions.MessageNotUnderstood;


public abstract class Connection {
  private static Log log = LogFactory.getLog(Connection.class);

  private char sequence = '0';

  private int connectionTimeout;
  private int idleTimeout;
  private int retryAttempts;
  private int retryWait;
  private String host;
  private int port;
  private boolean addSequenceAndChecksum = true;
  private boolean strictSequenceChecking = false;
  private boolean strictChecksumChecking = false;
  
  public void setAddSequenceAndChecksum(boolean flag)
  {
	  this.addSequenceAndChecksum = flag;
  }
  public boolean getAddSequenceAndChecksum() {
	  return this.addSequenceAndChecksum;
  }
  public void setStrictChecksumChecking(boolean flag) {
	  this.strictChecksumChecking = flag;
  }
  public boolean getStrictChecksumChecking() {
	  return this.strictChecksumChecking;
  }
  public void setStrictSequenceChecking(boolean flag)
  {
	  this.strictSequenceChecking = flag;
  }
  public boolean getStrictSequenceChecking()
  {
	  return this.strictSequenceChecking;
  }
  public void setHost(String host) {
    this.host = host;
  }
  public String getHost() {
    return host;
  }
  public void setPort(int port) {
    this.port = port;
  }
  public int getPort() {
    return port;
  }
  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }
  public int getConnectionTimeout() {
    return connectionTimeout;
  }
  public void setIdleTimeout(int idleTimeout) {
    this.idleTimeout = idleTimeout;
  }
  public int getIdleTimeout() {
    return idleTimeout;
  }
  public void setRetryAttempts(int retryAttempts) {
    this.retryAttempts = retryAttempts;
  }
  public int getRetryAttempts() {
    return retryAttempts;
  }
  public void setRetryWait(int retryWait) {
    this.retryWait = retryWait;
  }
  public int getRetryWait() {
    return retryWait;
  }

  private char getNextSequence() {
    char ret = sequence;
    sequence++;
    if (sequence > '9') {
      sequence = '0';
    }
    return ret;
  }

  protected String strim(String input) {
    String ret = input;

    while (ret.charAt(0) == 0 && ret.length() > 0) {
      ret = ret.substring(1);

    }
    return ret;
  }

  public synchronized void connect() throws Exception {
    connect(this.getRetryAttempts());
  }

  protected abstract void connect(int retryAttempts) throws Exception;
  public abstract boolean isConnected();
  public abstract void disconnect();

  protected abstract void internalSend(String msg) throws ConnectionFailure;
  protected abstract String internalWaitfor(String match) throws ConnectionFailure;

  public void send(String msg) throws ConnectionFailure {
    Timer timer = null;
    long timeout = this.getIdleTimeout();
    if (timeout > 0) {
      timer = new Timer();
      timer.schedule(new KillConnectionTask(this), timeout);
    }
    try {
      internalSend(msg);
    } catch (ConnectionFailure ex) {
      if (timer != null) {
        timer.cancel();
      }
      throw ex;
    }
    if (timer != null) {
      timer.cancel();
    }
  }

  public String waitfor(String match) throws ConnectionFailure {
    String ret = null;
    Timer timer = null;
    long timeout = this.getIdleTimeout();
    if (timeout > 0) {
      timer = new Timer();
      timer.schedule(new KillConnectionTask(this), timeout);
    }
    try {
      ret = internalWaitfor(match);
    } catch (ConnectionFailure ex) {
      if (timer != null) {
        timer.cancel();
      }
      throw ex;
    }
    if (timer != null) {
      timer.cancel();
    }
    return ret;
  }

  public synchronized Message send(Message msg) throws ConnectionFailure, RetriesExceeded, ChecksumError, SequenceError, MessageNotUnderstood, MandatoryFieldOmitted {
    String request, response = null;
    Message responseMessage = null;
    if (msg == null) {
      throw new MessageNotUnderstood(); //This will signal a corrupted data
    }
    try {
      boolean retry;
      boolean understood = false;
      int retries = 0;
      do {
        retry = false;
        try {
          if (this.getAddSequenceAndChecksum()) {
        	  request = msg.encode(new Character(getNextSequence()));
          } else {
        	  request = msg.encode(null);        	  
          }
          log.debug(">>> " + request);
          send(request);
          response = waitfor("\r");
          response = strim(response);
          log.debug("<<< " + response);
          if (this.getStrictSequenceChecking()) {
        	  responseMessage = Message.decode(response, sequence, this.getStrictChecksumChecking());
          } else {
        	  responseMessage=  Message.decode(response, null, this.getStrictChecksumChecking());    	  
          }
          if (responseMessage instanceof SCResend) {
            throw new ConnectionFailure();
          }
          understood = true;
        }
        catch (ConnectionFailure ex) {
          try {
            Thread.sleep(this.getRetryWait());
          }
          catch (Exception ex1) {
            log.debug("Thread sleep error", ex1);
          }
          retry = true;
          retries++;
          if (retries > this.getRetryAttempts()) {
            if (understood) {
              throw new RetriesExceeded();
            } else {
              throw new MessageNotUnderstood();
            }
          }
        }
      }
      while (retry);
      if (responseMessage == null) {
        throw new ConnectionFailure();
      }    
      return responseMessage;
    }
    catch (RetriesExceeded e) {
      throw e;
    }
  }
}

class KillConnectionTask extends TimerTask {
  private static Log log = LogFactory.getLog(TimerTask.class);
  private Connection connection;

  public KillConnectionTask(Connection conn) {
    this.connection = conn;
  }

  public void run() {
    try {
      log.error("Attempting to force close timed out connection");
      this.connection.disconnect();
    }
    catch (Exception ex) {
      log.error("Force closed timed out connection failed", ex);
    }
  }
}
