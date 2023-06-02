package at.fhooe.smartmeter.chart;

import android.graphics.Canvas;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class DoubleXLabelAxisRenderer extends XAxisRenderer {

    private HistoryXAxisFormatter topValueFormatter;

    public DoubleXLabelAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer transformer, HistoryXAxisFormatter topValueFormatter) {
        super(viewPortHandler, xAxis, transformer);
        this.topValueFormatter = topValueFormatter;
    }

    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        float yoffset = mXAxis.getYOffset();

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setColor(mXAxis.getTextColor());

        MPPointF pointF = MPPointF.getInstance(0, 0);
        if (mXAxis.getPosition() == XAxis.XAxisPosition.TOP) {
            pointF.x = 0.5f;
            pointF.y = 1.0f;
            drawLabels(c, mViewPortHandler.contentTop() - yoffset, pointF);

        } else if (mXAxis.getPosition() == XAxis.XAxisPosition.TOP_INSIDE) {
            pointF.x = 0.5f;
            pointF.y = 1.0f;
            drawLabels(c, mViewPortHandler.contentTop() + yoffset + mXAxis.mLabelRotatedHeight, pointF);

        } else if (mXAxis.getPosition() == XAxis.XAxisPosition.BOTTOM) {
            pointF.x = 0.5f;
            pointF.y = 0.0f;
            drawLabels(c, mViewPortHandler.contentBottom() + yoffset, pointF);

        } else if (mXAxis.getPosition() == XAxis.XAxisPosition.BOTTOM_INSIDE) {
            pointF.x = 0.5f;
            pointF.y = 0.0f;
            drawLabels(c, mViewPortHandler.contentBottom() - yoffset - mXAxis.mLabelRotatedHeight, pointF);

        } else { // BOTH SIDED
            pointF.x = 0.5f;
            pointF.y = 1.0f;
            drawLabelsTop(c, mViewPortHandler.contentTop() - yoffset, pointF);
            pointF.x = 0.5f;
            pointF.y = 0.0f;
            drawLabels(c, mViewPortHandler.contentBottom() + yoffset, pointF);
        }
        MPPointF.recycleInstance(pointF);
    }

    private void drawLabelsTop(Canvas c, float pos, MPPointF anchor) {

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

        float[] positions = new float[mXAxis.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {

            // only fill x values
            if (centeringEnabled) {
                positions[i] = mXAxis.mCenteredEntries[i / 2];
            } else {
                positions[i] = mXAxis.mEntries[i / 2];
            }
        }

        mTrans.pointValuesToPixel(positions);

        for (int i = 0; i < positions.length; i += 2) {

            float x = positions[i];

            if (mViewPortHandler.isInBoundsX(x)) {

                String label = topValueFormatter.getTopLabel(mXAxis.mEntries[i / 2], mXAxis);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                        if (width > mViewPortHandler.offsetRight() * 2
                                && x + width > mViewPortHandler.getChartWidth())
                            x -= width / 2;

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);
                        x += width / 2;
                    }
                }

                drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees);
            }
        }
    }
}
