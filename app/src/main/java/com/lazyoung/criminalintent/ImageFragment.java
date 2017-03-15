package com.lazyoung.criminalintent;
import android.graphics.drawable.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;

public class ImageFragment extends DialogFragment {
     public static final String EXTRA_IMAGE_PATH = "com.lazyoung.criminalintent.image_path";
     private ImageView mImageView;
     public static final String EXTRA_IMAGE_DEGREE = "com.lazyoung.criminalintent.image_degree";
     
     public static ImageFragment newInstance(String imagePath, int degree) {
         Bundle args = new Bundle();
         args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
         args.putInt(EXTRA_IMAGE_DEGREE, degree);
         ImageFragment fragment = new ImageFragment();
         fragment.setArguments(args);
         fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

         return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        mImageView = new ImageView(getActivity());
        String path = (String)getArguments().getSerializable(EXTRA_IMAGE_PATH);
        int degrees = getArguments().getInt(EXTRA_IMAGE_DEGREE);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path, degrees);

        mImageView.setImageDrawable(image);

        return mImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }
}
