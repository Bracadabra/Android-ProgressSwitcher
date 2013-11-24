package com.devspark.progressfragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class ContentSwitcher {

	private Context mContext;
	private View mRootView;
	private View mProgressContainer;
	private View mContentContainer;
	private View mContentView;
	private View mEmptyView;
	private View mErrorView;
	private boolean mContentShown;
	private boolean mIsContentEmpty;
	private boolean mIsErrorOccurred;
	private int mAnimationIn = android.R.anim.fade_in;
	private int mAnimationOut = android.R.anim.fade_out;

	public static ContentSwitcher prepare(final Context context, final Fragment fragment) {
		final ContentSwitcher contentSwicher = new ContentSwitcher(context);
		contentSwicher.setRootView(fragment.getView());
		final View contentView = contentSwicher.findContentView();
		contentSwicher.setContentView(contentView);

		return contentSwicher;
	}

	public ContentSwitcher(final Context context) {
		mContext = context;
	}

	/**
	 * Return content view or null if the content view has not been initialized.
	 * 
	 * @return content view or null
	 * @see #addContentView(android.view.View)
	 * @see #addContentView(int)
	 */
	public View getContentView() {
		return mContentView;
	}

	/**
	 * Set the content content from a layout resource.
	 * 
	 * @param layoutResId
	 *            Resource ID to be inflated.
	 * @see #addContentView(android.view.View)
	 * @see #getContentView()
	 */
	public void addContentView(final int layoutResId) {
		final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		final View contentView = layoutInflater.inflate(layoutResId, null);
		addContentView(contentView);
	}

	/**
	 * Set the content view to an explicit view. If the content view was
	 * installed earlier, the content will be replaced with a new view.
	 * 
	 * @param view
	 *            The desired content to display. Value can't be null.
	 * @see #addContentView(int)
	 * @see #getContentView()
	 */
	public void addContentView(final View view) {
		ensureContent();
		if (view == null) {
			throw new IllegalArgumentException("Content view can't be null");
		}
		if (mContentContainer instanceof ViewGroup) {
			ViewGroup contentContainer = (ViewGroup) mContentContainer;
			if (mContentView == null) {
				contentContainer.addView(view);
			} else {
				final int index = contentContainer.indexOfChild(mContentView);
				// replace content view
				contentContainer.removeView(mContentView);
				contentContainer.addView(view, index);
			}
			mContentView = view;
		} else {
			throw new IllegalStateException("Can't be used with a custom content view");
		}
	}

	public void setContentView(final int contentViewId) {
		ensureContent();
		if (mRootView == null) {
			throw new IllegalStateException("Root view not yet set");
		}
		if (mContentContainer instanceof ViewGroup) {
			mContentView = mRootView.findViewById(contentViewId);
		} else {
			throw new IllegalStateException("Can't be used with a custom content view");
		}
		if (mContentView == null) {
			throw new IllegalStateException("View with id "
					+ Integer.toHexString(contentViewId) + " wasn't found");
		}
	}

	public void setContentView(final View contentView) {
		ensureContent();
		if (mRootView == null) {
			throw new IllegalStateException("Root view not yet set");
		}
		if (contentView == null) {
			throw new IllegalStateException("Content view can't be null");
		}
		mContentView = contentView;
	}

	/**
	 * The default content for a ProgressFragment has a TextView that can be
	 * shown when the content is empty {@link #setContentEmpty(boolean)}. If you
	 * would like to have it shown, call this method to supply the text it
	 * should use.
	 * 
	 * @param resId
	 *            Identification of string from a resources
	 * @see #setEmptyText(CharSequence)
	 */
	public void setEmptyText(final int resId) {
		setEmptyText(mContext.getString(resId));
	}

	/**
	 * The default content for a ProgressFragment has a TextView that can be
	 * shown when the content is empty {@link #setContentEmpty(boolean)}. If you
	 * would like to have it shown, call this method to supply the text it
	 * should use.
	 * 
	 * @param text
	 *            Text for empty view
	 * @see #setEmptyText(int)
	 */
	public void setEmptyText(final CharSequence text) {
		ensureContent();
		if (mEmptyView != null && mEmptyView instanceof TextView) {
			((TextView) mEmptyView).setText(text);
		} else {
			throw new IllegalStateException("Can't be used with a custom content view");
		}
	}

	public void setErrorText(final int resId) {
		setErrorText(mContext.getString(resId));
	}

	public void setErrorText(final CharSequence text) {
		ensureContent();
		if (mErrorView != null && mErrorView instanceof TextView) {
			((TextView) mEmptyView).setText(text);
		} else {
			throw new IllegalStateException("Can't be used with a custom content view");
		}
	}

	/**
	 * Control whether the content is being displayed. You can make it not
	 * displayed if you are waiting for the initial data to show in it. During
	 * this time an indeterminant progress indicator will be shown instead.
	 * 
	 * @param shown
	 *            If true, the content view is shown; if false, the progress
	 *            indicator. The initial value is true.
	 * @see #setContentShownNoAnimation(boolean)
	 */
	public void setContentShown(final boolean shown) {
		setContentShown(shown, true);
	}

	/**
	 * Like {@link #setContentShown(boolean)}, but no animation is used when
	 * transitioning from the previous state.
	 * 
	 * @param shown
	 *            If true, the content view is shown; if false, the progress
	 *            indicator. The initial value is true.
	 * @see #setContentShown(boolean)
	 */
	public void setContentShownNoAnimation(final boolean shown) {
		setContentShown(shown, false);
	}

	/**
	 * Returns true if content is empty. The default content is not empty.
	 * 
	 * @return true if content is null or empty
	 * @see #setContentEmpty(boolean)
	 */
	public boolean isContentEmpty() {
		return mIsContentEmpty;
	}

	/**
	 * If the content is empty, then set true otherwise false. The default
	 * content is not empty. You can't call this method if the content view has
	 * not been initialized before {@link #addContentView(android.view.View)}
	 * and content view not null.
	 * 
	 * @param isEmpty
	 *            true if content is empty else false
	 * @see #isContentEmpty()
	 */
	public void setContentEmpty(final boolean isEmpty) {
		ensureContent();
		if (mContentView == null) {
			throw new IllegalStateException("Content view must be initialized before");
		}
		if (isEmpty) {
			mEmptyView.setVisibility(View.VISIBLE);
			mContentView.setVisibility(View.GONE);
			setErrorViewVisibility(View.GONE);
		} else {
			mEmptyView.setVisibility(View.GONE);
			mContentView.setVisibility(View.VISIBLE);
			setErrorViewVisibility(View.GONE);
		}
		mIsContentEmpty = isEmpty;
	}

	public void setRootView(final View root) {
		mRootView = root;
		ensureContent();
	}

	public void setErrorOccured(final boolean error) {
		if (mErrorView == null) {
			throw new IllegalStateException(
					"Error view should be specified in layout before");
		}
		if (mContentView == null) {
			throw new IllegalStateException("Content view must be initialized before");
		}

		if (error) {
			mErrorView.setVisibility(View.VISIBLE);
			mEmptyView.setVisibility(View.GONE);
			mContentView.setVisibility(View.GONE);
		} else {
			mErrorView.setVisibility(View.GONE);
			mEmptyView.setVisibility(View.GONE);
			mContentView.setVisibility(View.VISIBLE);
		}
		mIsErrorOccurred = error;
	}

	public boolean isErrorOccured() {
		return mIsErrorOccurred;
	}

	public void reset() {
		mContentShown = false;
		mIsContentEmpty = false;
		mIsErrorOccurred = false;
		mErrorView = mProgressContainer = mContentContainer = mContentView = mEmptyView = null;
	}

	public void setCustomAnimation(final int animationIn, final int animationOut) {
		mAnimationIn = animationIn;
		mAnimationOut = animationOut;
	}

	public void setOnErrorViewClickListener(final OnClickListener onClickListener) {
		if (mErrorView == null) {
			throw new IllegalStateException("Error view couldn't be null");
		}

		mErrorView.setOnClickListener(onClickListener);
	}

	public void setOnErrorViewClickListener(final OnClickListener onClickListener,
			final int resId) {
		if (mErrorView == null) {
			throw new IllegalStateException("Error view couldn't be null");
		}

		final View targetView = mErrorView.findViewById(resId);
		if (targetView == null) {
			throw new IllegalArgumentException("View with id " + resId + "wasn't found");
		}

		targetView.setOnClickListener(onClickListener);
	}

	private void setContentShown(final boolean shown, final boolean animate) {
		ensureContent();
		if (mContentShown == shown) {
			return;
		}
		mContentShown = shown;
		if (shown) {
			if (animate) {
				mProgressContainer.startAnimation(AnimationUtils.loadAnimation(mContext,
						mAnimationOut));
				mContentContainer.startAnimation(AnimationUtils.loadAnimation(mContext,
						mAnimationIn));
			} else {
				mProgressContainer.clearAnimation();
				mContentContainer.clearAnimation();

			}
			mProgressContainer.setVisibility(View.GONE);
			mContentContainer.setVisibility(View.VISIBLE);
		} else {
			if (animate) {
				mProgressContainer.startAnimation(AnimationUtils.loadAnimation(mContext,
						mAnimationIn));
				mContentContainer.startAnimation(AnimationUtils.loadAnimation(mContext,
						mAnimationOut));
			} else {
				mProgressContainer.clearAnimation();
				mContentContainer.clearAnimation();
			}
			mProgressContainer.setVisibility(View.VISIBLE);
			mContentContainer.setVisibility(View.GONE);
		}
	}

	private void ensureContent() {
		if (mContentContainer != null && mProgressContainer != null) {
			return;
		}
		if (mRootView == null) {
			throw new IllegalStateException("Root view not yet set");
		}
		mProgressContainer = mRootView.findViewById(R.id.progress_container);
		if (mProgressContainer == null) {
			throw new RuntimeException(
					"Your content must have a ViewGroup whose id attribute is 'R.id.progress_container'");
		}
		mContentContainer = mRootView.findViewById(R.id.content_container);
		if (mContentContainer == null) {
			throw new RuntimeException(
					"Your content must have a ViewGroup whose id attribute is 'R.id.content_container'");
		}
		mEmptyView = mRootView.findViewById(android.R.id.empty);
		if (mEmptyView != null) {
			mEmptyView.setVisibility(View.GONE);
		}
		mErrorView = mRootView.findViewById(R.id.error);
		if (mErrorView != null) {
			mErrorView.setVisibility(View.GONE);
		}
		mContentShown = true;
		// We are starting without a content, so assume we won't
		// have our data right away and start with the progress indicator.
		if (mContentView == null) {
			setContentShown(false, false);
		}
	}

	private void setErrorViewVisibility(final int visibility) {
		if (mErrorView != null) {
			mErrorView.setVisibility(visibility);
		}
	}

	private View findContentView() {
		View contentView = null;
		final ViewGroup contentContainer = (ViewGroup) mContentContainer;
		final int childCount = contentContainer.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View view = contentContainer.getChildAt(i);
			if (view != mEmptyView && view != mErrorView) {
				contentView = view;
				break;
			}
		}

		return contentView;
	}

}
