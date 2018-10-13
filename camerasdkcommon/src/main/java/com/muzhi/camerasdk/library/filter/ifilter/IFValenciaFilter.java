package com.muzhi.camerasdk.library.filter.ifilter;


import com.muzhi.camerasdk.library.utils.MResource;

import android.content.Context;

/**
 */
public class IFValenciaFilter extends IFImageFilter {
    private static final String SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2; //map\n" +
            " uniform sampler2D inputImageTexture3; //gradMap\n" +
            " \n" +
            " mat3 saturateMatrix = mat3(\n" +
            "                            1.1402,\n" +
            "                            -0.0598,\n" +
            "                            -0.061,\n" +
            "                            -0.1174,\n" +
            "                            1.0826,\n" +
            "                            -0.1186,\n" +
            "                            -0.0228,\n" +
            "                            -0.0228,\n" +
            "                            1.1772);\n" +
            " \n" +
            " vec3 lumaCoeffs = vec3(.3, .59, .11);\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     vec3 texel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "     \n" +
            "     texel = vec3(\n" +
            "                  texture2D(inputImageTexture2, vec2(texel.r, .1666666)).r,\n" +
            "                  texture2D(inputImageTexture2, vec2(texel.g, .5)).g,\n" +
            "                  texture2D(inputImageTexture2, vec2(texel.b, .8333333)).b\n" +
            "                  );\n" +
            "     \n" +
            "     texel = saturateMatrix * texel;\n" +
            "     float luma = dot(lumaCoeffs, texel);\n" +
            "     texel = vec3(\n" +
            "                  texture2D(inputImageTexture3, vec2(luma, texel.r)).r,\n" +
            "                  texture2D(inputImageTexture3, vec2(luma, texel.g)).g,\n" +
            "                  texture2D(inputImageTexture3, vec2(luma, texel.b)).b);\n" +
            "     \n" +
            "     gl_FragColor = vec4(texel, 1.0);\n" +
            " }\n";

    public IFValenciaFilter(Context context) {
        super(context, SHADER);
        setRes(context);
    }

    private void setRes(Context context) {
    	int resId1=MResource.getIdByName(context,MResource.drawable, "valencia_map");
    	int resId2=MResource.getIdByName(context,MResource.drawable, "valencia_gradient_map");
    	addInputTexture(resId1);
        addInputTexture(resId2);
        
       /* addInputTexture(R.drawable.valencia_map);
        addInputTexture(R.drawable.valencia_gradient_map);*/
    }
}
