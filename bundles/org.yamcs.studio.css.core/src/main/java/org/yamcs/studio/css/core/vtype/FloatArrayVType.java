package org.yamcs.studio.css.core.vtype;

import java.util.List;

import org.diirt.util.array.ArrayFloat;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ListFloat;
import org.diirt.util.array.ListInt;
import org.diirt.vtype.ArrayDimensionDisplay;
import org.diirt.vtype.VFloatArray;
import org.diirt.vtype.VTypeToString;
import org.diirt.vtype.ValueUtil;
import org.yamcs.protobuf.Pvalue.ParameterValue;

public class FloatArrayVType extends YamcsVType implements VFloatArray {

    private ListInt sizes;
    private List<ArrayDimensionDisplay> dimensionDisplay;

    private ListFloat data;

    public FloatArrayVType(ParameterValue pval) {
        super(pval);

        int size = pval.getEngValue().getArrayValueCount();
        sizes = new ArrayInt(size);
        dimensionDisplay = ValueUtil.defaultArrayDisplay(sizes);

        float[] floatValues = new float[size];
        for (int i = 0; i < floatValues.length; i++) {
            floatValues[i] = pval.getEngValue().getArrayValue(i).getFloatValue();
        }
        data = new ArrayFloat(floatValues);
    }

    @Override
    public ListInt getSizes() {
        return sizes;
    }

    @Override
    public ListFloat getData() {
        return data;
    }

    @Override
    public List<ArrayDimensionDisplay> getDimensionDisplay() {
        return dimensionDisplay;
    }

    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }
}
