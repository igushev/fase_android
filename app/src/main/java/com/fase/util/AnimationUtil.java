package com.fase.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

public class AnimationUtil {

    public static void animateVisibility(View container, boolean show) {
        ObjectAnimator mover = ObjectAnimator.ofFloat(container, "translationY", show ? -1000 : 0, show ? 0 : -1000);
        mover.setDuration(400);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(container, "alpha", show ? 0f : 1f, show ? 1f : 0f);
        fadeIn.setDuration(300);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(mover, fadeIn);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (show) {
                    container.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) {
                    container.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.start();
    }

    public static ObjectAnimator createRotateAnimatorForExpandableIndicator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    public static ValueAnimator createColorChangeAnimatorForExpandableTitle(TextView textView, int fromColor, int toColor) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                ContextCompat.getColor(textView.getContext(), fromColor), ContextCompat.getColor(textView.getContext(), toColor));
        colorAnimation.addUpdateListener(animator -> textView.setTextColor((int) animator.getAnimatedValue()));
        colorAnimation.setDuration(300);
        return colorAnimation;
    }

    public static void animateFilter(View mainContainer, View filterContainer, View background, boolean show) {
        ObjectAnimator mover = ObjectAnimator.ofFloat(filterContainer, "translationY", show ? -1000 : 0, show ? 0 : -1000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(background, "alpha", show ? 0f : 0.5f, show ? 0.5f : 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(mover, fadeIn);
        animatorSet.setDuration(400);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (show) {
                    mainContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) {
                    mainContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.start();
    }

    public static void animateClearButton(View clearButton, boolean show) {
        animateButton(clearButton, -150f, 0f, show);
    }

    private static void animateButton(View button, float goneX, float visibleX, boolean show) {
        ObjectAnimator mover = ObjectAnimator.ofFloat(button, "translationX", show ? goneX : visibleX, show ? visibleX : goneX);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(button, "alpha", show ? 0f : 1f, show ? 1f : 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(mover, fadeIn);
        animatorSet.setDuration(400);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (show) {
                    button.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) {
                    button.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.start();
    }

    public static ObjectAnimator animateAlphaVisibility(View view, boolean show) {
        return animateAlphaVisibility(view, show, 0f, 1f, 300);
    }

    public static ObjectAnimator animateAlphaVisibility(View view, boolean show, float from, float to, int duration) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", show ? from : to, show ? to : from);
        fadeIn.setDuration(duration);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (show) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setVisibility(show ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        fadeIn.start();
        return fadeIn;
    }
}
