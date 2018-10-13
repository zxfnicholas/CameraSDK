package com.muzhi.camerasdk.library.filter.ifilter;


import com.muzhi.camerasdk.library.utils.MResource;

import android.content.Context;

/**
 */
public class IFInkwellFilter extends IFImageFilter {
    private static final String SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     vec3 texel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "     texel = vec3(dot(vec3(0.3, 0.6, 0.1), texel));\n" +
            "     texel = vec3(texture2D(inputImageTexture2, vec2(texel.r, .16666)).r);\n" +
            "     gl_FragColor = vec4(texel, 1.0);\n" +
            " }\n";

    public IFInkwellFilter(Context context) {
        super(context, SHADER);
        setRes(context);
    }

    private void setRes(Context context) {
    	
    	int resId1=MResource.getIdByName(context,MResource.drawable, "inkwell_map");
    	addInputTexture(resId1);
        
       // addInputTexture(R.drawable.inkwell_map);
    }
}
