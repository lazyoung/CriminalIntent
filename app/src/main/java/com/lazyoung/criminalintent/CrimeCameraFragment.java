package com.lazyoung.criminalintent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.io.IOException;
import java.util.List;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;
import android.annotation.TargetApi;
import java.util.*;
import java.io.*;
import android.content.*;
import android.app.*;
import android.widget.*;
import android.view.*;

public class CrimeCameraFragment extends Fragment {
    private static final String TAG = "CrimeCameraFragment";
    public static final String EXTRA_PHOTO_FILENAME = "com.lazyoung.criminalintent.photo_filename";
    private Camera mCamera;
    private SurfaceView mSurfaceView;
	  private View mProgressContainer;
	
	  private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		
    public void onShutter() {
			// Display the progress indicator
			mProgressContainer.setVisibility(View.VISIBLE);
		}
	};

	private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// Create a filename
			String filename = UUID.randomUUID().toString() + ".jpg";
			// Save the jpeg data to disk
			FileOutputStream os = null;
			boolean success = true;

			try {
				os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
				os.write(data);
			} catch (Exception e) {
				Log.e(TAG, "Error writing to file " + filename, e);
				success = false;
			}  finally {
				try {
					if (os != null)
						os.close();
				} catch  (Exception  e) {
					Log.e(TAG, "Error closing file " + filename, e);
					success = false;
				}
			}
			
			// Set the photo filename on the result intent
			if (success) {
				Intent i = new Intent();
				i.putExtra(EXTRA_PHOTO_FILENAME, filename);
				getActivity().setResult(Activity.RESULT_OK, i);
			} else {
				getActivity().setResult(Activity.RESULT_CANCELED);
			}
			getActivity().finish();
		}
	};
			

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);

        mProgressContainer = v.findViewById(R.id.cime_camera_progressContainer);
		    mProgressContainer.setVisibility(View.INVISIBLE);
		    ImageButton takePictureButton = (ImageButton)v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
				if  (mCamera !=  null) {
					mCamera.takePicture(mShutterCallback, null, mJpegCallback);
				}   
            }
        });

        mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        // setType() and SURFACE_TYPE_PUSH_BUFFERS are both deprecated, but are required for Camera preview to work on pre-3.0 devices
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //Tell Camera to use this surface as its preview area
                try {
                    if(mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException exception) {
                    Log.e(TAG, "Error setting up preview display", exception);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if(mCamera == null) return;

                //The surface has changed size; update the camera preview size
                Camera.Parameters parameters = mCamera.getParameters();
                Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
                parameters.setPreviewSize(s.width, s.height);
				        s = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
				        parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);
                setCameraDisplayOrientation(getActivity(), 1, mCamera);
                
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                //We can no longer display on this surface, so stop the preview
                if(mCamera != null) {
                    mCamera.stopPreview();
                }
            }
        });

        return v;
    }

	@Override
	@TargetApi(9)
	public void onResume()
	{
		super.onResume();
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
		    mCamera = Camera.open(1);
		}
		else {
			mCamera = Camera.open();
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();

		if(mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

    /** A simple algorithm to get the largest size available.For a more *
     *  robust version, see CameraPreview. java in the ApiDemos
     *  * sample app from Android. */
    @SuppressWarnings("deprecation")
    private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
        Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }

        return bestSize;
    }
    
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
            new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}


