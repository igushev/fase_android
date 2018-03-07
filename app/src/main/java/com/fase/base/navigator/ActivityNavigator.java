package com.fase.base.navigator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.fase.R;
import com.fase.base.BaseFragment;
import com.fase.base.PlainConsumer;
import com.fase.model.enums.ActivityTransition;
import com.fase.model.enums.FragmentTransition;

import java.lang.ref.WeakReference;

public class ActivityNavigator implements Navigator {

    public static final String ACTIVITY_TRANSITION_ENTER = "activityTransitionEnter";
    public static final String ACTIVITY_TRANSITION_EXIT = "activityTransitionExit";

    private final WeakReference<FragmentActivity> mActivityReference;

    public ActivityNavigator(FragmentActivity activity) {
        mActivityReference = new WeakReference<>(activity);
    }

    @Override
    public void finishActivity() {
        finishActivity(false);
    }

    @Override
    public void finishActivity(boolean affinity) {
        FragmentActivity fragmentActivity = mActivityReference.get();
        if (fragmentActivity != null) {
            if (affinity) {
                fragmentActivity.finishAffinity();
            } else {
                fragmentActivity.finish();
            }
        }
    }

    @Override
    public final void startActivity(@NonNull String action) {
        FragmentActivity fragmentActivity = mActivityReference.get();
        if (fragmentActivity != null) {
            fragmentActivity.startActivity(new Intent(action));
        }
    }

    @Override
    public void startActivity(@NonNull Class<? extends Activity> activityClass) {
        startActivityInternal(activityClass, null, null, false);
    }

    @Override
    public void startActivity(@NonNull Class<? extends Activity> activityClass, boolean clearTop) {
        startActivityInternal(activityClass, null, null, clearTop);
    }

    @Override
    public void startActivity(@NonNull Class<? extends Activity> activityClass, Bundle args) {
        startActivityInternal(activityClass, intent -> intent.putExtra(EXTRA_ARG, args), null, false);
    }

    @Override
    public void startActivity(@NonNull Class<? extends Activity> activityClass, Bundle args, boolean clearTop) {
        startActivityInternal(activityClass, intent -> intent.putExtra(EXTRA_ARG, args), null, clearTop);
    }

    @Override
    public final void startActivityForResult(@NonNull Class<? extends Activity> activityClass, int requestCode) {
        startActivityInternal(activityClass, null, requestCode, false);
    }

    @Override
    public void startActivityForResult(@NonNull Class<? extends Activity> activityClass, Bundle args, int requestCode) {
        startActivityInternal(activityClass, intent -> intent.putExtra(EXTRA_ARG, args), requestCode, false);
    }

    private void startActivityInternal(Class<? extends Activity> activityClass, PlainConsumer<Intent> setArgsAction, Integer requestCode, boolean clearTop) {
        FragmentActivity fragmentActivity = mActivityReference.get();
        if (fragmentActivity != null) {
            Intent intent = new Intent(fragmentActivity, activityClass);
            if (setArgsAction != null) {
                setArgsAction.accept(intent);
            }
            if (clearTop) {
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            if (requestCode != null) {
                fragmentActivity.startActivityForResult(intent, requestCode);
            } else {
                fragmentActivity.startActivity(intent);
            }
        }
    }

    @Override
    public void replaceFragment(@NonNull BaseFragment fragment) {
        replaceFragmentInternal(R.id.container, fragment, false, FragmentTransition.FADE);
    }

    @Override
    public void replaceFragment(@NonNull BaseFragment fragment, FragmentTransition transition) {
        replaceFragmentInternal(R.id.container, fragment, false, transition);
    }

    @Override
    public void replaceFragment(int containerId, @NonNull BaseFragment fragment) {
        replaceFragmentInternal(containerId, fragment, false, FragmentTransition.FADE);
    }

    @Override
    public void replaceFragment(int containerId, @NonNull BaseFragment fragment, FragmentTransition transition) {
        replaceFragmentInternal(containerId, fragment, false, transition);
    }

    @Override
    public void replaceFragment(@NonNull BaseFragment fragment, boolean addToBackStack) {
        replaceFragmentInternal(R.id.container, fragment, addToBackStack, FragmentTransition.FADE);
    }

    @Override
    public void replaceFragment(@NonNull BaseFragment fragment, boolean addToBackStack, FragmentTransition transition) {
        replaceFragmentInternal(R.id.container, fragment, addToBackStack, transition);
    }

    @Override
    public void replaceFragment(int containerId, @NonNull BaseFragment fragment, boolean addToBackStack) {
        replaceFragmentInternal(containerId, fragment, addToBackStack, FragmentTransition.FADE);
    }

    @Override
    public void replaceFragment(int containerId, @NonNull BaseFragment fragment, boolean addToBackStack, FragmentTransition transition) {
        replaceFragmentInternal(containerId, fragment, addToBackStack, transition);
    }

    private void replaceFragmentInternal(@IdRes int containerId, BaseFragment fragment, boolean addToBackStack, FragmentTransition transition) {
        FragmentActivity fragmentActivity = mActivityReference.get();
        if (fragmentActivity != null) {
            FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
            Fragment oldFragment = fragmentManager.findFragmentById(containerId);
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (transition) {
                case OPEN:
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    break;
                case CLOSE:
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    break;
                case FADE:
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    break;
                case SLIDE_LEFT:
                    transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                    break;
                case SLIDE_RIGHT:
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                    break;
            }

            if (oldFragment != null) {
                transaction.detach(oldFragment);
            }

            transaction.replace(containerId, fragment, fragment.getTag());
            if (addToBackStack) {
                transaction.addToBackStack(fragment.getTag()).commit();
                fragmentManager.executePendingTransactions();
            } else {
                transaction.commitNow();
            }
        }
    }

    @Override
    public void clearBackStack(boolean exceptFirst) {
        FragmentActivity fragmentActivity = mActivityReference.get();
        if (fragmentActivity != null) {
            if (exceptFirst) {
                if (fragmentActivity.getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    FragmentManager.BackStackEntry entry = fragmentActivity.getSupportFragmentManager().getBackStackEntryAt(0);
                    fragmentActivity.getSupportFragmentManager().popBackStack(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentActivity.getSupportFragmentManager().executePendingTransactions();
                }
            } else {
                fragmentActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    @Override
    public void closeFragment() {
        FragmentActivity fragmentActivity = mActivityReference.get();
        if (fragmentActivity != null) {
            fragmentActivity.getSupportFragmentManager().popBackStack();
        }
    }

    @NonNull
    public static Bundle putTransitionsToBundle(ActivityTransition enterTransition, ActivityTransition exitTransition) {
        return putTransitionsToBundle(new Bundle(), enterTransition, exitTransition);
    }

    @NonNull
    public static Bundle putTransitionsToBundle(Bundle bundle, ActivityTransition enterTransition, ActivityTransition exitTransition) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (enterTransition != null) {
            bundle.putSerializable(ACTIVITY_TRANSITION_ENTER, enterTransition);
        }
        if (exitTransition != null) {
            bundle.putSerializable(ACTIVITY_TRANSITION_EXIT, exitTransition);
        }

        return bundle;
    }
}
