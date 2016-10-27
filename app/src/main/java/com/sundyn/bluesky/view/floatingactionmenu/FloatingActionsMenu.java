package com.sundyn.bluesky.view.floatingactionmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.sundyn.bluesky.R;

public class FloatingActionsMenu extends ViewGroup {
    public static final int EXPAND_UP = 0;
    public static final int EXPAND_DOWN = 1;
    public static final int EXPAND_LEFT = 2;
    public static final int EXPAND_RIGHT = 3;

    public static final int LABELS_ON_LEFT_SIDE = 0;
    public static final int LABELS_ON_RIGHT_SIDE = 1;

    private static final int ANIMATION_DURATION = 300;
    private static final float COLLAPSED_PLUS_ROTATION = 0f;
    private static final float EXPANDED_PLUS_ROTATION = 90f + 45f;

    private int mAddButtonColorNormal;
    private int mAddButtonColorPressed;
    private int mAddButtonSize;
    private boolean mAddButtonStrokeVisible;
    private int mExpandDirection;

    private int mButtonSpacing;
    private int mLabelsMargin;
    private int mLabelsVerticalOffset;
    private int mAnimScroll;

    private boolean mExpanded;

    private AnimatorSet mExpandAnimation = new AnimatorSet()
            .setDuration(ANIMATION_DURATION);
    private AnimatorSet mCollapseAnimation = new AnimatorSet()
            .setDuration(ANIMATION_DURATION);
    private FloatingActionButton mAddButton;
    private RotatingDrawable mRotatingDrawable;
    private int mMaxButtonWidth;
    private int mMaxButtonHeight;
    private int mLabelsStyle;
    private int mLabelsPosition;
    private int mButtonsCount;
    private Drawable mDrawableIcon;
    private int mScrollThreshold;
    private boolean mVisible = true;
    private boolean mFloatingVisible;

    private TouchDelegateGroup mTouchDelegateGroup;

    private OnFloatingActionsMenuUpdateListener mListener;

    public interface OnFloatingActionsMenuUpdateListener {
        void onMenuExpanded();

        void onMenuCollapsed();
    }

    public FloatingActionsMenu(Context context) {
        this(context, null);
    }

    public FloatingActionsMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatingActionsMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        mButtonSpacing = (int) (getResources().getDimension(
                R.dimen.fab_actions_spacing)
                - getResources().getDimension(R.dimen.fab_shadow_radius) - getResources()
                .getDimension(R.dimen.fab_shadow_offset));
        mLabelsMargin = getResources().getDimensionPixelSize(
                R.dimen.fab_labels_margin);
        mLabelsVerticalOffset = getResources().getDimensionPixelSize(
                R.dimen.fab_shadow_offset);
        mScrollThreshold = getResources().getDimensionPixelOffset(
                R.dimen.fab_scroll_threshold);

        mFloatingVisible = true;

        mTouchDelegateGroup = new TouchDelegateGroup(this);
        setTouchDelegate(mTouchDelegateGroup);

        TypedArray attr = context.obtainStyledAttributes(attributeSet,
                R.styleable.FloatingActionsMenu, 0, 0);
        // mAddButtonPlusColor =
        // attr.getColor(R.styleable.FloatingActionsMenu_fab_addButtonPlusIconColor,
        // getColor(android.R.color.white));
        mAddButtonColorNormal = attr.getColor(
                R.styleable.FloatingActionsMenu_fab_addButtonColorNormal,
                getColor(R.color.default_normal));
        mAddButtonColorPressed = attr.getColor(
                R.styleable.FloatingActionsMenu_fab_addButtonColorPressed,
                getColor(R.color.default_pressed));
        mAddButtonSize = attr.getInt(
                R.styleable.FloatingActionsMenu_fab_addButtonSize,
                FloatingActionButton.SIZE_NORMAL);
        mAddButtonStrokeVisible = attr.getBoolean(
                R.styleable.FloatingActionsMenu_fab_addButtonStrokeVisible,
                true);
        mExpandDirection = attr.getInt(
                R.styleable.FloatingActionsMenu_fab_expandDirection, EXPAND_UP);
        mLabelsStyle = attr.getResourceId(
                R.styleable.FloatingActionsMenu_fab_labelStyle, 0);
        mLabelsPosition = attr.getInt(
                R.styleable.FloatingActionsMenu_fab_labelsPosition,
                LABELS_ON_LEFT_SIDE);
        mAnimScroll = attr.getInt(
                R.styleable.FloatingActionsMenu_fab_animationScroll,
                FabAnimationUtils.ANIM_NONE);
        mDrawableIcon = attr
                .getDrawable(R.styleable.FloatingActionsMenu_fab_menuIcon);

        // By default, use add (plus) icon
        if (mDrawableIcon == null) {
            mDrawableIcon = getIconDrawable(context);
        }
        attr.recycle();

        if (mLabelsStyle != 0 && expandsHorizontally()) {
            throw new IllegalStateException(
                    "Action labels in horizontal expand orientation is not supported.");
        }

        createAddButton(context);
    }

    public void setOnFloatingActionsMenuUpdateListener(
            OnFloatingActionsMenuUpdateListener listener) {
        mListener = listener;
    }

    public void setIcon(Drawable drawable) {
        if (drawable == null) {
            throw new NullPointerException(
                    "FloatingActionsMenu setIcon can not be null!");
        }
        mDrawableIcon = drawable;
        mAddButton.setIconDrawable(mDrawableIcon);

    }

    private boolean expandsHorizontally() {
        return mExpandDirection == EXPAND_LEFT
                || mExpandDirection == EXPAND_RIGHT;
    }

    private static class RotatingDrawable extends LayerDrawable {
        public RotatingDrawable(Drawable drawable) {
            super(new Drawable[]{drawable});
        }

        private float mRotation;

        @SuppressWarnings("UnusedDeclaration")
        public float getRotation() {
            return mRotation;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setRotation(float rotation) {
            mRotation = rotation;
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.rotate(mRotation, getBounds().centerX(), getBounds()
                    .centerY());
            super.draw(canvas);
            canvas.restore();
        }
    }

    private void createAddButton(Context context) {

        mAddButton = new FloatingActionButton(context) {
            @Override
            void updateBackground() {
                // mPlusColor = mAddButtonPlusColor;
                mColorNormal = mAddButtonColorNormal;
                mColorPressed = mAddButtonColorPressed;
                mStrokeVisible = mAddButtonStrokeVisible;
                super.updateBackground();
            }

            @Override
            Drawable getIconDrawable() {

                final RotatingDrawable rotatingDrawable = new RotatingDrawable(
                        mDrawableIcon);
                mRotatingDrawable = rotatingDrawable;

                final OvershootInterpolator interpolator = new OvershootInterpolator();

                final ObjectAnimator collapseAnimator = ObjectAnimator.ofFloat(
                        rotatingDrawable, "rotation", EXPANDED_PLUS_ROTATION,
                        COLLAPSED_PLUS_ROTATION);
                final ObjectAnimator expandAnimator = ObjectAnimator.ofFloat(
                        rotatingDrawable, "rotation", COLLAPSED_PLUS_ROTATION,
                        EXPANDED_PLUS_ROTATION);

                collapseAnimator.setInterpolator(interpolator);
                expandAnimator.setInterpolator(interpolator);

                mExpandAnimation.play(expandAnimator);
                mCollapseAnimation.play(collapseAnimator);

                return rotatingDrawable;
            }
        };
        if (mDrawableIcon != null)
            mAddButton.setIconDrawable(mDrawableIcon);
        mAddButton.setId(R.id.fab_expand_menu_button);
        mAddButton.setSize(mAddButtonSize);
        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        addView(mAddButton, super.generateDefaultLayoutParams());
        mButtonsCount++;
    }

    public void addButton(FloatingActionButton button) {
        addView(button, mButtonsCount - 1);
        mButtonsCount++;

        if (mLabelsStyle != 0) {
            createLabels();
        }
    }

    public void removeButton(FloatingActionButton button) {
        removeView(button.getLabelView());
        removeView(button);
        button.setTag(R.id.fab_label, null);
        mButtonsCount--;
    }

    /**
     * state of floatingActionMenu visible or not, available or disable
     * scrolling handler
     *
     * @param visible : true if enable scrolling animation and visible state false
     *                if disable scrolling animation and gone state
     */
    public void setVisible(boolean visible) {
        setVisible(visible, false);
    }

    /**
     * state of floatingActionMenu visible or not, available or disable
     * scrolling handler
     *
     * @param visible : true if enable scrolling animation and visible state false
     *                if disable scrolling animation and gone state
     */
    public void setVisibleWithAnimation(boolean visible) {
        setVisible(visible, true);
    }

    private void setVisible(boolean visible, boolean animation) {
        if (mVisible == visible) {
            return;
        }
        mVisible = visible;
        if (animation) {
            FabAnimationUtils.scale(this, mAddButton, visible);
        } else
            this.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private int getColor(@ColorRes int id) {
        return getResources().getColor(id);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        mMaxButtonWidth = 0;
        mMaxButtonHeight = 0;
        int maxLabelWidth = 0;

        for (int i = 0; i < mButtonsCount; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            switch (mExpandDirection) {
                case EXPAND_UP:
                case EXPAND_DOWN:
                    mMaxButtonWidth = Math.max(mMaxButtonWidth,
                            child.getMeasuredWidth());
                    height += child.getMeasuredHeight();
                    break;
                case EXPAND_LEFT:
                case EXPAND_RIGHT:
                    width += child.getMeasuredWidth();
                    mMaxButtonHeight = Math.max(mMaxButtonHeight,
                            child.getMeasuredHeight());
                    break;
            }

            if (!expandsHorizontally()) {
                TextView label = (TextView) child.getTag(R.id.fab_label);
                if (label != null) {
                    maxLabelWidth = Math.max(maxLabelWidth,
                            label.getMeasuredWidth());
                }
            }
        }

        if (!expandsHorizontally()) {
            width = mMaxButtonWidth
                    + (maxLabelWidth > 0 ? maxLabelWidth + mLabelsMargin : 0);
        } else {
            height = mMaxButtonHeight;
        }

        switch (mExpandDirection) {
            case EXPAND_UP:
            case EXPAND_DOWN:
                height += mButtonSpacing * (mButtonsCount - 1);
                height = adjustForOvershoot(height);
                break;
            case EXPAND_LEFT:
            case EXPAND_RIGHT:
                width += mButtonSpacing * (mButtonsCount - 1);
                width = adjustForOvershoot(width);
                break;
        }

        setMeasuredDimension(width, height);
    }

    private int adjustForOvershoot(int dimension) {
        return dimension * 12 / 10;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        switch (mExpandDirection) {
            case EXPAND_UP:
            case EXPAND_DOWN:
                boolean expandUp = mExpandDirection == EXPAND_UP;

                if (changed) {
                    mTouchDelegateGroup.clearTouchDelegates();
                }


                int addButtonY = expandUp ? b - t - mAddButton.getMeasuredHeight()
                        : 0;
    // Ensure mAddButton is centered on the line where the buttons
                int buttonsHorizontalCenter = mLabelsPosition == LABELS_ON_LEFT_SIDE ? r
                        - l - mMaxButtonWidth / 2
                        : mMaxButtonWidth / 2;
                int addButtonLeft = buttonsHorizontalCenter
                        - mAddButton.getMeasuredWidth() / 2;
                mAddButton.layout(addButtonLeft, addButtonY, addButtonLeft
                                + mAddButton.getMeasuredWidth(),
                        addButtonY + mAddButton.getMeasuredHeight());

                int labelsOffset = mMaxButtonWidth / 2 + mLabelsMargin;
                int labelsXNearButton = mLabelsPosition == LABELS_ON_LEFT_SIDE ? buttonsHorizontalCenter
                        - labelsOffset
                        : buttonsHorizontalCenter + labelsOffset;

                int nextY = expandUp ? addButtonY - mButtonSpacing : addButtonY
                        + mAddButton.getMeasuredHeight() + mButtonSpacing;

                for (int i = mButtonsCount - 1; i >= 0; i--) {
                    final View child = getChildAt(i);

                    if (child == mAddButton || child.getVisibility() == GONE)
                        continue;

                    int childX = buttonsHorizontalCenter - child.getMeasuredWidth()
                            / 2;
                    int childY = expandUp ? nextY - child.getMeasuredHeight()
                            : nextY;
                    child.layout(childX, childY, childX + child.getMeasuredWidth(),
                            childY + child.getMeasuredHeight());

                    float collapsedTranslation = addButtonY - childY;
                    float expandedTranslation = 0f;

                    ViewHelper.setTranslationY(child,
                            mExpanded ? expandedTranslation : collapsedTranslation);
                    ViewHelper.setAlpha(child, mExpanded ? 1f : 0f);

                    LayoutParams params = (LayoutParams) child.getLayoutParams();
                    params.mCollapseDir.setFloatValues(expandedTranslation,
                            collapsedTranslation);
                    params.mExpandDir.setFloatValues(collapsedTranslation,
                            expandedTranslation);
                    params.setAnimationsTarget(child);

                    View label = (View) child.getTag(R.id.fab_label);
                    if (label != null) {
                        int labelXAwayFromButton = mLabelsPosition == LABELS_ON_LEFT_SIDE ? labelsXNearButton
                                - label.getMeasuredWidth()
                                : labelsXNearButton + label.getMeasuredWidth();

                        int labelLeft = mLabelsPosition == LABELS_ON_LEFT_SIDE ? labelXAwayFromButton
                                : labelsXNearButton;

                        int labelRight = mLabelsPosition == LABELS_ON_LEFT_SIDE ? labelsXNearButton
                                : labelXAwayFromButton;

                        int labelTop = childY
                                - mLabelsVerticalOffset
                                + (child.getMeasuredHeight() - label
                                .getMeasuredHeight()) / 2;

                        label.layout(labelLeft, labelTop, labelRight, labelTop
                                + label.getMeasuredHeight());

                        Rect touchArea = new Rect(Math.min(childX, labelLeft),
                                childY - mButtonSpacing / 2, Math.max(childX
                                + child.getMeasuredWidth(), labelRight),
                                childY + child.getMeasuredHeight() + mButtonSpacing
                                        / 2);
                        mTouchDelegateGroup.addTouchDelegate(new TouchDelegate(
                                touchArea, child));

                        ViewHelper.setTranslationY(label,
                                mExpanded ? expandedTranslation
                                        : collapsedTranslation);
                        ViewHelper.setAlpha(label, mExpanded ? 1f : 0f);

                        LayoutParams labelParams = (LayoutParams) label
                                .getLayoutParams();
                        labelParams.mCollapseDir.setFloatValues(
                                expandedTranslation, collapsedTranslation);
                        labelParams.mExpandDir.setFloatValues(collapsedTranslation,
                                expandedTranslation);
                        labelParams.setAnimationsTarget(label);
                    }

                    nextY = expandUp ? childY - mButtonSpacing : childY
                            + child.getMeasuredHeight() + mButtonSpacing;
                }
                break;

            case EXPAND_LEFT:
            case EXPAND_RIGHT:
                boolean expandLeft = mExpandDirection == EXPAND_LEFT;

                int addButtonX = expandLeft ? r - l - mAddButton.getMeasuredWidth()
                        : 0;
                // Ensure mAddButton is centered on the line where the buttons
                // should be
                int addButtonTop = b - t - mMaxButtonHeight
                        + (mMaxButtonHeight - mAddButton.getMeasuredHeight()) / 2;
                mAddButton.layout(addButtonX, addButtonTop,
                        addButtonX + mAddButton.getMeasuredWidth(), addButtonTop
                                + mAddButton.getMeasuredHeight());

                int nextX = expandLeft ? addButtonX - mButtonSpacing : addButtonX
                        + mAddButton.getMeasuredWidth() + mButtonSpacing;

                for (int i = mButtonsCount - 1; i >= 0; i--) {
                    final View child = getChildAt(i);

                    if (child == mAddButton || child.getVisibility() == GONE)
                        continue;

                    int childX = expandLeft ? nextX - child.getMeasuredWidth()
                            : nextX;
                    int childY = addButtonTop
                            + (mAddButton.getMeasuredHeight() - child
                            .getMeasuredHeight()) / 2;
                    child.layout(childX, childY, childX + child.getMeasuredWidth(),
                            childY + child.getMeasuredHeight());

                    float collapsedTranslation = addButtonX - childX;
                    float expandedTranslation = 0f;

                    ViewHelper.setTranslationX(child,
                            mExpanded ? expandedTranslation : collapsedTranslation);
                    ViewHelper.setAlpha(child, mExpanded ? 1f : 0f);

                    LayoutParams params = (LayoutParams) child.getLayoutParams();
                    params.mCollapseDir.setFloatValues(expandedTranslation,
                            collapsedTranslation);
                    params.mExpandDir.setFloatValues(collapsedTranslation,
                            expandedTranslation);
                    params.setAnimationsTarget(child);

                    nextX = expandLeft ? childX - mButtonSpacing : childX
                            + child.getMeasuredWidth() + mButtonSpacing;
                }

                break;
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(super.generateDefaultLayoutParams());
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(super.generateLayoutParams(attrs));
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(
            ViewGroup.LayoutParams p) {
        return new LayoutParams(super.generateLayoutParams(p));
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p);
    }

    private static Interpolator sExpandInterpolator = new OvershootInterpolator();
    private static Interpolator sCollapseInterpolator = new DecelerateInterpolator(
            3f);
    private static Interpolator sAlphaExpandInterpolator = new DecelerateInterpolator();

    private class LayoutParams extends ViewGroup.LayoutParams {

        private ObjectAnimator mExpandDir = new ObjectAnimator();
        private ObjectAnimator mExpandAlpha = new ObjectAnimator();
        private ObjectAnimator mCollapseDir = new ObjectAnimator();
        private ObjectAnimator mCollapseAlpha = new ObjectAnimator();
        private boolean animationsSetToPlay;

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);

            mExpandDir.setInterpolator(sExpandInterpolator);
            mExpandAlpha.setInterpolator(sAlphaExpandInterpolator);
            mCollapseDir.setInterpolator(sCollapseInterpolator);
            mCollapseAlpha.setInterpolator(sCollapseInterpolator);

            mCollapseAlpha.setPropertyName("alpha");
            mCollapseAlpha.setFloatValues(1f, 0f);

            mExpandAlpha.setPropertyName("alpha");
            mExpandAlpha.setFloatValues(0f, 1f);

            switch (mExpandDirection) {
                case EXPAND_UP:
                case EXPAND_DOWN:
                    mCollapseDir.setPropertyName("translationY");
                    mExpandDir.setPropertyName("translationY");
                    break;
                case EXPAND_LEFT:
                case EXPAND_RIGHT:
                    mCollapseDir.setPropertyName("translationX");
                    mExpandDir.setPropertyName("translationX");
                    break;
            }
        }

        public void setAnimationsTarget(View view) {
            mCollapseAlpha.setTarget(view);
            mCollapseDir.setTarget(view);
            mExpandAlpha.setTarget(view);
            mExpandDir.setTarget(view);

            // Now that the animations have targets, set them to be played
            if (!animationsSetToPlay) {
                addLayerTypeListener(mExpandDir, view);
                addLayerTypeListener(mCollapseDir, view);

                mCollapseAnimation.play(mCollapseAlpha);
                mCollapseAnimation.play(mCollapseDir);
                mExpandAnimation.play(mExpandAlpha);
                mExpandAnimation.play(mExpandDir);
                animationsSetToPlay = true;
            }
        }

        private void addLayerTypeListener(Animator animator, final View view) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ViewCompat.setLayerType(view, ViewCompat.LAYER_TYPE_NONE,
                            null);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    ViewCompat.setLayerType(view,
                            ViewCompat.LAYER_TYPE_HARDWARE, null);
                }
            });
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        bringChildToFront(mAddButton);
        mButtonsCount = getChildCount();

        if (mLabelsStyle != 0) {
            createLabels();
        }
    }

    private void createLabels() {
        Context context = new ContextThemeWrapper(getContext(), mLabelsStyle);

        for (int i = 0; i < mButtonsCount; i++) {
            FloatingActionButton button = (FloatingActionButton) getChildAt(i);
            String title = button.getTitle();

            if (button == mAddButton || title == null
                    || button.getTag(R.id.fab_label) != null)
                continue;

            TextView label = new TextView(context);
            label.setTextAppearance(getContext(), mLabelsStyle);
            label.setText(button.getTitle());
            addView(label);

            button.setTag(R.id.fab_label, label);
        }
    }

    public void collapse() {
        collapse(false);
    }

    public void collapseImmediately() {
        collapse(true);
    }

    private void collapse(boolean immediately) {
        if (mExpanded) {
            mExpanded = false;
            mTouchDelegateGroup.setEnabled(false);
            mCollapseAnimation
                    .setDuration(immediately ? 0 : ANIMATION_DURATION);
            mCollapseAnimation.start();
            mExpandAnimation.cancel();

            if (mListener != null) {
                mListener.onMenuCollapsed();
            }
        }
    }

    public void toggle() {
        if (mExpanded) {
            collapse();
        } else {
            expand();
        }
    }

    public void expand() {
        if (!mExpanded) {
            mExpanded = true;
            mTouchDelegateGroup.setEnabled(true);
            mCollapseAnimation.cancel();
            mExpandAnimation.start();

            if (mListener != null) {
                mListener.onMenuExpanded();
            }
        }
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        mAddButton.setEnabled(enabled);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.mExpanded = mExpanded;

        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            mExpanded = savedState.mExpanded;
            mTouchDelegateGroup.setEnabled(mExpanded);

            if (mRotatingDrawable != null) {
                mRotatingDrawable
                        .setRotation(mExpanded ? EXPANDED_PLUS_ROTATION
                                : COLLAPSED_PLUS_ROTATION);
            }

            super.onRestoreInstanceState(savedState.getSuperState());
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public static class SavedState extends BaseSavedState {
        public boolean mExpanded;

        public SavedState(Parcelable parcel) {
            super(parcel);
        }

        private SavedState(Parcel in) {
            super(in);
            mExpanded = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mExpanded ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    // =============Adding listener=========

    private void show() {
        if (mVisible) {
            toggleScroll(true);
        }
    }

    private void hide() {
        if (mVisible) {
            toggleScroll(false);
        }
    }

    private int getMarginBottom() {
        int marginBottom = 0;
        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        }
        return marginBottom;
    }

    private int getMarginRight() {
        int marginRight = 0;
        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginRight = ((MarginLayoutParams) layoutParams).rightMargin;
        }
        return marginRight;
    }

    private void toggleScroll(final boolean visible) {
        if (mFloatingVisible != visible) {
            mFloatingVisible = visible;
            if (isExpanded())
                collapse();

            switch (mAnimScroll) {
                case FabAnimationUtils.ANIM_NONE:
                    break;
                case FabAnimationUtils.ANIM_TRANSLATION_Y:
                    int height = getHeight();
                    if (height == 0) {
                        ViewTreeObserver vto = getViewTreeObserver();
                        if (vto.isAlive()) {
                            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    ViewTreeObserver currentVto = getViewTreeObserver();
                                    if (currentVto.isAlive()) {
                                        currentVto.removeOnPreDrawListener(this);
                                    }
                                    toggleScroll(visible);
                                    return true;
                                }
                            });
                            return;
                        }
                    }
                    FabAnimationUtils.translationY(this, visible, height,
                            getMarginBottom());
                    break;
                case FabAnimationUtils.ANIM_SCALE:
                    FabAnimationUtils.scale(this, mAddButton, visible);
                    break;
                case FabAnimationUtils.ANIM_FADE:
                    FabAnimationUtils.fade(this, visible);
                    break;
                case FabAnimationUtils.ANIM_TRANSLATION_X:
                    int width = getWidth();
                    if (width == 0) {
                        ViewTreeObserver vto = getViewTreeObserver();
                        if (vto.isAlive()) {
                            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    ViewTreeObserver currentVto = getViewTreeObserver();
                                    if (currentVto.isAlive()) {
                                        currentVto.removeOnPreDrawListener(this);
                                    }
                                    toggleScroll(visible);
                                    return true;
                                }
                            });
                            return;
                        }
                    }
                    FabAnimationUtils.translationX(this, mAddButton, visible,
                            width, getMarginRight());
                    break;
            }

            // On pre-Honeycomb a translated view is still clickable, so we need
            // to disable clicks manually
            if (!hasHoneycombApi()) {
                setClickAbleAll(visible);
            }
        }

    }

    public void attachToListView(@NonNull AbsListView listView) {
        attachToListView(listView, null, null);
    }

    public void attachToListView(@NonNull AbsListView listView,
                                 ScrollDirectionListener scrollDirectionListener) {
        attachToListView(listView, scrollDirectionListener, null);
    }

    public void attachToScrollView(@NonNull ObservableScrollView scrollView) {
        attachToScrollView(scrollView, null, null);
    }

    public void attachToScrollView(@NonNull ObservableScrollView scrollView,
                                   ScrollDirectionListener scrollDirectionListener) {
        attachToScrollView(scrollView, scrollDirectionListener, null);
    }

    public void attachToListView(@NonNull AbsListView listView,
                                 ScrollDirectionListener scrollDirectionListener,
                                 AbsListView.OnScrollListener onScrollListener) {
        AbsListViewScrollDetectorImpl scrollDetector = new AbsListViewScrollDetectorImpl();
        scrollDetector.setScrollDirectionListener(scrollDirectionListener);
        scrollDetector.setOnScrollListener(onScrollListener);
        scrollDetector.setListView(listView);
        scrollDetector.setScrollThreshold(mScrollThreshold);
        listView.setOnScrollListener(scrollDetector);
    }

    public void attachToScrollView(@NonNull ObservableScrollView scrollView,
                                   ScrollDirectionListener scrollDirectionListener,
                                   ObservableScrollView.OnScrollChangedListener onScrollChangedListener) {
        ScrollViewScrollDetectorImpl scrollDetector = new ScrollViewScrollDetectorImpl();
        scrollDetector.setScrollDirectionListener(scrollDirectionListener);
        scrollDetector.setOnScrollChangedListener(onScrollChangedListener);
        scrollDetector.setScrollThreshold(mScrollThreshold);
        scrollView.setOnScrollChangedListener(scrollDetector);
    }

    private boolean hasJellyBeanApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    private boolean hasHoneycombApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    private class AbsListViewScrollDetectorImpl extends
            AbsListViewScrollDetector {
        private ScrollDirectionListener mScrollDirectionListener;
        private AbsListView.OnScrollListener mOnScrollListener;

        private void setScrollDirectionListener(
                ScrollDirectionListener scrollDirectionListener) {
            mScrollDirectionListener = scrollDirectionListener;
        }

        public void setOnScrollListener(
                AbsListView.OnScrollListener onScrollListener) {
            mOnScrollListener = onScrollListener;
        }

        @Override
        public void onScrollDown() {
            show();
            if (mScrollDirectionListener != null) {
                mScrollDirectionListener.onScrollDown();
            }
        }

        @Override
        public void onScrollUp() {
            hide();
            if (mScrollDirectionListener != null) {
                mScrollDirectionListener.onScrollUp();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScroll(view, firstVisibleItem,
                        visibleItemCount, totalItemCount);
            }

            super.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(view, scrollState);
            }

            super.onScrollStateChanged(view, scrollState);
        }
    }

    private class ScrollViewScrollDetectorImpl extends ScrollViewScrollDetector {
        private ScrollDirectionListener mScrollDirectionListener;

        private ObservableScrollView.OnScrollChangedListener mOnScrollChangedListener;

        private void setScrollDirectionListener(
                ScrollDirectionListener scrollDirectionListener) {
            mScrollDirectionListener = scrollDirectionListener;
        }

        public void setOnScrollChangedListener(
                ObservableScrollView.OnScrollChangedListener onScrollChangedListener) {
            mOnScrollChangedListener = onScrollChangedListener;
        }

        @Override
        public void onScrollDown() {
            show();
            if (mScrollDirectionListener != null) {
                mScrollDirectionListener.onScrollDown();
            }
        }

        @Override
        public void onScrollUp() {
            hide();
            if (mScrollDirectionListener != null) {
                mScrollDirectionListener.onScrollUp();
            }
        }

        @Override
        public void onScrollChanged(ScrollView who, int l, int t, int oldl,
                                    int oldt) {
            if (mOnScrollChangedListener != null) {
                mOnScrollChangedListener.onScrollChanged(who, l, t, oldl, oldt);
            }

            super.onScrollChanged(who, l, t, oldl, oldt);
        }
    }

    private void setClickAbleAll(boolean clickable) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.setEnabled(clickable);
        }
    }

    private Drawable getIconDrawable(Context context) {
        final float iconSize = context.getResources().getDimension(
                R.dimen.fab_icon_size);
        final float iconHalfSize = iconSize / 2f;

        final float plusSize = context.getResources().getDimension(
                R.dimen.fab_plus_icon_size);
        final float plusHalfStroke = context.getResources().getDimension(
                R.dimen.fab_plus_icon_stroke) / 2f;
        final float plusOffset = (iconSize - plusSize) / 2f;

        final Shape shape = new Shape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                canvas.drawRect(plusOffset, iconHalfSize - plusHalfStroke,
                        iconSize - plusOffset, iconHalfSize + plusHalfStroke,
                        paint);
                canvas.drawRect(iconHalfSize - plusHalfStroke, plusOffset,
                        iconHalfSize + plusHalfStroke, iconSize - plusOffset,
                        paint);
            }
        };

        ShapeDrawable drawable = new ShapeDrawable(shape);

        final Paint paint = drawable.getPaint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        return drawable;
    }
}
