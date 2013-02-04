package com.gst_rtp_client.gst_rtp_client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gstreamer.GStreamer;

public class GstRtpClient extends Activity implements SurfaceHolder.Callback, OnTouchListener, SensorEventListener {
	private static final String TAG = "7D-DEMO-JOYSTICK";
	private static final int FILTER_PART_OF_VIEW = 3;
	private static final long JOYSTICK_RESENDING_DELAY = 100;
	static JoystickConnectionServerThread mJoystickConnectionThread;
	JoystickMessageQueueThread mJoystickMessageQueueThread;
	int mViewWidth;
	int mViewHeight;
	private SurfaceView mSurfaceView;
    private static JoystickMessagingHandler mHandler;
	private SensorManager mSensorManager;
	private float[] mGravity = new float[3];
	private float[] mGeomag = new float[3];
	private float[] mRotationMatrix = new float[16];
	private static final int CALIBRAITON_CICLES = 10;
	private int mCalibration = CALIBRAITON_CICLES;
	private int mInitPhi = 0;
	private int mInitTheta = 0;
	private boolean mSensorIsStreaming = false;

    private native void nativeInit();     // Initialize native code, build pipeline, etc
    private native void nativeFinalize(); // Destroy pipeline and shutdown native code
    private native void nativePlay();     // Set pipeline to PLAYING
    private native void nativePause();    // Set pipeline to PAUSED
    private static native boolean nativeClassInit(); // Initialize native class: cache Method IDs for callbacks
    private native void nativeSurfaceInit(Object surface);
    private native void nativeSurfaceFinalize();
    private long native_custom_data;      // Native code will use this to keep private data

    private boolean is_playing_desired;   // Whether the user asked to go to PLAYING

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mJoystickConnectionThread = new JoystickConnectionServerThread("TCP Connection Server for HOST thread");
        mJoystickConnectionThread.start();
        mJoystickMessageQueueThread = new JoystickMessageQueueThread("Message queue thread");
        mJoystickMessageQueueThread.start();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Initialize GStreamer and warn if it fails
        try {
            GStreamer.init(this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish(); 
            return;
        }

        setContentView(R.layout.main);

        ImageButton play = (ImageButton) this.findViewById(R.id.button_play);
        play.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                is_playing_desired = true;
                nativePlay();
                startSensorDataStreaming();

            }
        });

        ImageButton pause = (ImageButton) this.findViewById(R.id.button_stop);
        pause.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                is_playing_desired = false;
                nativePause();
                stopSensorDataStreaming();
            }
        });

        mSurfaceView = (SurfaceView) this.findViewById(R.id.surface_video);
        SurfaceHolder sh = mSurfaceView.getHolder();
        sh.addCallback(this);
        mSurfaceView.setOnTouchListener(this);
        
        if (savedInstanceState != null) {
            is_playing_desired = savedInstanceState.getBoolean("playing");
            Log.i (TAG, "Activity created. Saved state is playing:" + is_playing_desired);
        } else {
            is_playing_desired = false;
            Log.i (TAG, "Activity created. There is no saved state, playing: false");
        }

        // Start with disabled buttons, until native code is initialized
        this.findViewById(R.id.button_play).setEnabled(false);
        this.findViewById(R.id.button_stop).setEnabled(false);

        nativeInit();

    }
    
    private void startSensorDataStreaming() {
    	mInitPhi = 0;
    	mInitTheta = 0;
    	mCalibration = CALIBRAITON_CICLES;
    	if (mSensorIsStreaming) {
    		return;
    	}
    	mSensorIsStreaming = true;
		mHandler.sendMessage(Message.obtain(mHandler,JoystickMessagingHandler.MOTION_TURN_RESENDING_MESSAGE));
    }
    
    private void stopSensorDataStreaming() {
    	if (mSensorIsStreaming == false) {
    		return;
    	}
    	mSensorIsStreaming = false;
		mHandler.sendMessage(Message.obtain(mHandler,JoystickMessagingHandler.MOTION_TURN_STOP_RESENDING_MESSAGE));
    }
    
    protected void onSaveInstanceState (Bundle outState) {
        Log.d (TAG, "Saving state, playing:" + is_playing_desired);
        outState.putBoolean("playing", is_playing_desired);
    }

    protected void onDestroy() {
        nativeFinalize();
        super.onDestroy();
    }

    // Called from native code. This sets the content of the TextView from the UI thread.
    private void setMessage(final String message) {
        final TextView tv = (TextView) this.findViewById(R.id.textview_message);
        runOnUiThread (new Runnable() {
          public void run() {
            tv.setText(message);
          }
        });
    }

    // Called from native code. Native code calls this once it has created its pipeline and
    // the main loop is running, so it is ready to accept commands.
    private void onGStreamerInitialized () {
        Log.i (TAG, "Gst initialized. Restoring state, playing:" + is_playing_desired);
        // Restore previous playing state
        if (is_playing_desired) {
            nativePlay();
        } else {
            nativePause();
        }

        // Re-enable buttons, now that GStreamer is initialized
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            public void run() {
                activity.findViewById(R.id.button_play).setEnabled(true);
                activity.findViewById(R.id.button_stop).setEnabled(true);
            }
        });
    }

    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("bz2");
        System.loadLibrary("gst_rtp_client");
        System.loadLibrary("gstudp");
        nativeClassInit();
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        Log.d(TAG, "Surface changed to format " + format + " width "
                + width + " height " + height);
        nativeSurfaceInit (holder.getSurface());
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Surface created: " + holder.getSurface());
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface destroyed");
        nativeSurfaceFinalize ();
    }
    
    static private class JoystickMessagingHandler extends Handler {
        private static final int MOTION_MOVE_RESENDING_MESSAGE = 1;
        private static final int MOTION_MOVE_STOP_RESENDING_MESSAGE = 2;
        private static final int MOTION_TURN_RESENDING_MESSAGE = 3;
        private static final int MOTION_TURN_STOP_RESENDING_MESSAGE = 4;
        private static final int MOTION_MOVE_MESSAGE = 5;
        private static final int MOTION_TURN_MESSAGE = 6;
        private boolean mResendingMove = false;
        private boolean mResendingTurn = false;


        @Override
        public void handleMessage(Message msg) {
            try {
				if (msg.what == MOTION_MOVE_RESENDING_MESSAGE) {
					mResendingMove = true;
					mHandler.sendMessage(Message.obtain(mHandler,
							MOTION_MOVE_MESSAGE));
				}

				if (msg.what == MOTION_MOVE_STOP_RESENDING_MESSAGE) {
					mResendingMove = false;
				}

				if (msg.what == MOTION_TURN_RESENDING_MESSAGE) {
					mResendingTurn = true;
					mHandler.sendMessage(Message.obtain(mHandler,
							MOTION_TURN_MESSAGE));
				}

				if (msg.what == MOTION_TURN_STOP_RESENDING_MESSAGE) {
					mResendingTurn = false;
				}

				if (msg.what == MOTION_MOVE_MESSAGE) {
					mJoystickConnectionThread.pushMoveData();
					if (mResendingMove) {
						mHandler.sendMessageDelayed(Message.obtain(mHandler,
								MOTION_MOVE_MESSAGE), JOYSTICK_RESENDING_DELAY);
					}
				}
				if (msg.what == MOTION_TURN_MESSAGE) {
					mJoystickConnectionThread.pushTurnData();
					if (mResendingTurn) {
						mHandler.sendMessageDelayed(Message.obtain(mHandler,
								MOTION_TURN_MESSAGE), JOYSTICK_RESENDING_DELAY);
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "JoystickMessagingHandler:", e);
            }
        }
    }
    
    private class JoystickMessageQueueThread extends Thread {
        public JoystickMessageQueueThread(String name) {
            super(name);
        }

        public void run() {
            Log.d(TAG, "joystick messaging queue thread");
    		Looper.prepare();
			mHandler = new JoystickMessagingHandler();
			Looper.loop();
        }
    }

    private class JoystickConnectionServerThread extends Thread {
    	private static final byte TURN_THRESHOLD = 5;
        private ServerSocket      mServerSocket = null;
        private static final int  SERVERPORT = 9979;
        private Socket			  mTransportSocket = null; 
        private byte[]	  		  mControlBlock = null;
        private OutputStream	  mOut = null;
        private byte mLeftStrafe = 0;
        private byte mRightStrafe = 0;
        private byte mMoveUp = 0;
		private byte mMoveDown = 0;
        private byte mTurnPhi = 0;
		private byte mTurnTheta = 0;
		
        public JoystickConnectionServerThread(String name) {
            super(name);
            mControlBlock = new byte[6];
		}
        
		public void setMoveData(byte left_strafe, byte right_strafe,
				byte move_up, byte move_down) {
			mLeftStrafe = left_strafe;
			mRightStrafe = right_strafe;
			mMoveDown = move_down;
			mMoveUp = move_up;
		}
		
		public void setTurnData(byte turn_phi, byte turn_theta) {
			mTurnPhi = turn_phi;
			mTurnTheta = turn_theta;
			if (Math.abs(mTurnPhi) < TURN_THRESHOLD) {
				mTurnPhi = 0;
			}
			if (Math.abs(mTurnTheta) < TURN_THRESHOLD) {
				mTurnTheta = 0;
			}
		}
		
		public void pushMoveData() {
			if (mTransportSocket != null && mOut != null) {
				mControlBlock[0] = mLeftStrafe;
				mControlBlock[1] = mRightStrafe;
				mControlBlock[2] = mMoveUp;
				mControlBlock[3] = mMoveDown;
				mControlBlock[4] = 0;
				mControlBlock[5] = 0;
				try {
					mOut.write(mControlBlock, 0, 6);
				} catch (IOException e) {
					Log.e(TAG, "Cannot push control block to host");
					e.printStackTrace();
				}
			}
		}
        
		public void pushTurnData() {
			Log.d(TAG, "Turn data mTurnPhi["+mTurnPhi+"], mTurnTheta["+mTurnTheta+"]");
			if (mTransportSocket != null && mOut != null &&
					(mTurnPhi != 0 || mTurnTheta != 0)) {
				mControlBlock[0] = 0;
				mControlBlock[1] = 0;
				mControlBlock[2] = 0;
				mControlBlock[3] = 0;
				mControlBlock[4] = mTurnPhi;
				mControlBlock[5] = mTurnTheta;
				try {
					mOut.write(mControlBlock, 0, 6);
				} catch (IOException e) {
					Log.e(TAG, "Cannot push control block to host");
					e.printStackTrace();
				}
			}
		}

		public void run() {
	
			Log.d(TAG, "medialib connection manager thread");
            try {
                mServerSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "ServerSocket["+Integer.toString(SERVERPORT)+"] is successfully opened");
            try {
                mServerSocket.setReuseAddress(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                	mTransportSocket = mServerSocket.accept();
                	mOut = mTransportSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

	public byte isRightStrafe(int x, int y) {
		int width = mSurfaceView.getWidth();
		int filterViewX = width / FILTER_PART_OF_VIEW;
		if (x < filterViewX) {
			return 1;
		}
		return 0;
	}

	public byte isLeftStrafe(int x, int y) {
		int width = mSurfaceView.getWidth();
		int filterViewX = width / FILTER_PART_OF_VIEW;
		if (x > (width - filterViewX)) {
			return 1;
		}
		return 0;
	}

	public byte isMoveUp(int x, int y) {
		int height = mSurfaceView.getHeight();
		int filterViewY = height / FILTER_PART_OF_VIEW;
		if (y < filterViewY) {
			return 1;
		}
		return 0;
	}

	public byte isMoveDown(int x, int y) {
		int height = mSurfaceView.getHeight();
		int filterViewY = height / FILTER_PART_OF_VIEW;
		if (y > (height - filterViewY)) {
			return 1;
		}
		return 0;
	}
    
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			mHandler.sendMessage(Message
					.obtain(mHandler,
							JoystickMessagingHandler.MOTION_MOVE_STOP_RESENDING_MESSAGE));
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mHandler.sendMessage(Message.obtain(mHandler,
					JoystickMessagingHandler.MOTION_MOVE_RESENDING_MESSAGE));
		}
		int x = (int) event.getX(0);
		int y = (int) event.getY(0);
		byte left_strafe = isLeftStrafe(x, y);
		byte right_strafe = isRightStrafe(x, y);
		byte move_up = isMoveUp(x, y);
		byte move_down = isMoveDown(x, y);
		if (event.getPointerCount() > 1) {
			x = (int) event.getX(1);
			y = (int) event.getY(1);
			if (left_strafe==0 && right_strafe==0){
				left_strafe = isLeftStrafe(x, y);
				right_strafe = isRightStrafe(x, y);			
			}
			if (move_up==0 && move_down==0){
				move_up = isMoveUp(x, y);
				move_down = isMoveDown(x, y);			
			}
		}
		if (left_strafe == 1 || right_strafe == 1 || move_up == 1 || move_down  == 1) {
			Log.d(TAG, "left_strafe["+left_strafe+"], right_strafe["+right_strafe+
					"], move_up["+move_up+"], move_down["+move_down+"]");
			mJoystickConnectionThread.setMoveData(left_strafe, right_strafe, move_up, move_down);
		}
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	//Normalize a degree from 0 to 360 instead of -180 to 180
	private int normalizeDegrees(double rads){
	    return (int)((rads+360)%360);
	}
	@Override
	public void onSensorChanged(SensorEvent evt) {
		int type = evt.sensor.getType();

		// Smoothing the sensor data a bit
		if (type == Sensor.TYPE_MAGNETIC_FIELD) {
				mGeomag[0] = (mGeomag[0] * 1 + evt.values[0]) * 0.5f;
				mGeomag[1] = (mGeomag[1] * 1 + evt.values[1]) * 0.5f;
				mGeomag[2] = (mGeomag[2] * 1 + evt.values[2]) * 0.5f;
		} else if (type == Sensor.TYPE_ACCELEROMETER) {
				mGravity[0] = (mGravity[0] * 2 + evt.values[0]) * 0.33334f;
				mGravity[1] = (mGravity[1] * 2 + evt.values[1]) * 0.33334f;
				mGravity[2] = (mGravity[2] * 2 + evt.values[2]) * 0.33334f;
		}

		if ((type == Sensor.TYPE_MAGNETIC_FIELD)
				|| (type == Sensor.TYPE_ACCELEROMETER)) {
			SensorManager.getRotationMatrix(mRotationMatrix, null, mGravity,
					mGeomag);
			SensorManager.remapCoordinateSystem(mRotationMatrix,
					SensorManager.AXIS_Y, SensorManager.AXIS_Z,
					mRotationMatrix);
            float values[] = new float[4];
            SensorManager.getOrientation(mRotationMatrix,values);
            int theta = normalizeDegrees(Math.toDegrees(values[1]));
            int phi = normalizeDegrees(Math.toDegrees(values[2]));
            if (mCalibration > 0) {
            	mInitPhi = (mInitPhi * 1 + phi)/2;
            	mInitTheta = (mInitTheta * 1 + theta)/2;
            	mCalibration--;
            	mJoystickConnectionThread.setTurnData((byte)0, (byte)0);
            } else {
            	mJoystickConnectionThread.setTurnData((byte)(mInitPhi-phi), (byte)(theta-mInitTheta));
            }
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

}
