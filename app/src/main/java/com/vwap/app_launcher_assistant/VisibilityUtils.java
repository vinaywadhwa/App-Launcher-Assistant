package com.vwap.app_launcher_assistant;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewAnimationUtils;
/**
 * Utilities to reduce boilerplate code around {@link View#setVisibility(int)} usage.
 */
@SuppressWarnings ("unused")
public class VisibilityUtils {
    /**
     * Show view(s) by setting visibility of the view(s) to {@link View#VISIBLE}
     *
     * @param views the view(s) to be shown
     */
    public static void show(View... views) {
        setVisible(true, views);
    }

    private static void setVisible(boolean shouldMakeVisible, View... views) {
        for (View view : views) {
            if (view != null) {
                if (shouldMakeVisible) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * Show view(s) by setting visibility of the view(s) to {@link View#VISIBLE}
     *
     * @param containerView the container in which the view(s) can be found.
     * @param viewIds       viewID(s) of the view(s) to be shown.
     */
    public static void show(View containerView, int viewIds) {
        setVisible(true, containerView, viewIds);
    }

    private static void setVisible(boolean shouldMakeVisible, View containerView, int viewId) {
        View view = containerView.findViewById(viewId);
        if (view != null) {
            if (shouldMakeVisible) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Hide view(s) by setting visibility of the view(s) to {@link View#GONE}
     *
     * @param views the view(s) to be hidden
     */
    public static void hide(View... views) {
        setVisible(false, views);
    }

    /**
     * Hide view(s) by setting visibility of the view(s) to {@link View#GONE}
     *
     * @param containerView the container in which the view(s) can be found.
     * @param viewIds       viewID(s) of the view(s) to be hidden.
     */
    public static void hide(View containerView, int viewIds) {
        setVisible(false, containerView, viewIds);
    }

    static void circularRevealAnimation(View v, boolean shouldShow) {

        try {
            if (shouldShow) {
                // get the center for the clipping circle
                int cx = v.getWidth() / 2;
                int cy = v.getHeight() / 2;
                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);
                // create the animator for this view (the start radius is zero)
                final Animator anim =
                        ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
                anim.setDuration(500);
                // make the view visible and start the animation
                v.setVisibility(View.VISIBLE);
                anim.start();
            } else {
                // previously visible view
                final View myView = v;
                // get the center for the clipping circle
                int cx = myView.getWidth() / 2;
                int cy = myView.getHeight() / 2;
                // get the initial radius for the clipping circle
                float initialRadius = (float) Math.hypot(cx, cy);
                // create the animation (the final radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);
                // make the view invisible when the animation is done
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        myView.setVisibility(View.GONE);
                    }
                });
                // start the animation
                anim.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
