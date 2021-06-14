package rocks.friedrich.one_two_three_weight;
// https://gist.githubusercontent.com/bmarrdev/f03835f5387ef3b64ca5/raw/dc6b98915a3e45a0009101252982db73d651790e/AnimateCounter.java
// https://androidbycode.wordpress.com/2015/07/11/animating-numerical-change-using-android-interpolators/
/*
 * Copyright (C) 2015 Hooked On Play
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.animation.Interpolator;
import android.widget.TextView;

/**
 * AnimateCounter provides ability to animate the changing of numbers using the builtin
 * Android Interpolator animation functionality.
 *
 */
public class AnimateCounter {
    /**
     * TextView to be animated
     */
    private final TextView view;
    /**
     * Duration of animation
     */
    private final long duration;
    /**
     * Initial value to start animation
     */
    private float startValue;
    /**
     * End value to finish animation
     */
    private final float endValue;
    /**
     * Decimal precision for floating point values
     */
    private final int precision;
    /**
     * Interpolator functionality to apply to animation
     */
    private final Interpolator interpolator;
    private ValueAnimator valueAnimator;

    /**
     * Provides optional callback functionality on completion of animation
     */
    private AnimateCounterListener listener;

    /**
     * Call to execute the animation
     */
    public void execute(){
        // Fix glitch with negative start values by points per portion
        if (startValue < 0) startValue = 0f;
        valueAnimator = ValueAnimator.ofFloat(startValue, endValue);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.addUpdateListener(valueAnimator -> {
            float current = Float.parseFloat(valueAnimator.getAnimatedValue().toString());
            view.setText(String.format("%." + precision + "f", current));
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.onAnimateCounterEnd();
                }
            }
        });

        valueAnimator.start();
    }

    public static class Builder {
        private long duration = 2000;
        private float startValue = 0;
        private float endValue = 10;
        private int precision = 0;
        private Interpolator interpolator = null;
        private final TextView view;

        public Builder(@NonNull TextView view) {
            this.view = view;
        }

        /**
         * Set the start and end integers to be animated
         *
         * @param start initial value
         * @param end final value
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCount(final int start, final int end) {
            if (start == end) {
                throw new IllegalArgumentException("Count start and end must be different");
            }

            startValue = start;
            endValue = end;
            precision = 0;
            return this;
        }

        /**
         * Set the start and end floating point numbers to be animated
         *
         * @param start initial value
         * @param end final value
         * @param precision number of decimal places to use
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCount(final float start, final float end, final int precision) {
            if (Math.abs(start - end) < 0.001) {
                throw new IllegalArgumentException("Count start and end must be different");
            }
            if (precision < 0) {
                throw new IllegalArgumentException("Precision can't be negative");
            }
            startValue = start;
            endValue = end;
            this.precision = precision;
            return this;
        }

        /**
         * Set the duration of the animation from start to end
         *
         * @param duration total duration of animation in ms
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setDuration(long duration) {
            if (duration <= 0) {
                throw new IllegalArgumentException("Duration must be positive value");
            }
            this.duration = duration;
            return this;
        }

        /**
         * Set the interpolator to be used with the animation
         *
         * @param interpolator Optional interpolator to set
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setInterpolator(@Nullable Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        /**
         * Creates a {@link AnimateCounter} with the arguments supplied to this builder. It does not
         * {@link AnimateCounter#execute()} the AnimationCounter.
         * Use {@link #execute()} to start the animation
         */
        public AnimateCounter build() {
            return new AnimateCounter(this);
        }
    }

    private AnimateCounter(Builder builder) {
        view = builder.view;
        duration = builder.duration;
        startValue = builder.startValue;
        endValue = builder.endValue;
        precision = builder.precision;
        interpolator = builder.interpolator;
    }

    /**
     * Stop the current animation
     */
    public void stop() {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
    }

    /**
     * Set a listener to get notification of completion of animation
     *
     * @param listener AnimationCounterListener to be used for callbacks
     */
    public void setAnimateCounterListener(AnimateCounterListener listener) {
        this.listener = listener;
    }

    /**
     * Callback interface for notification of animation end
     */
    public interface AnimateCounterListener {
        void onAnimateCounterEnd();
    }
}
