package com.muzhi.camerasdk.library.filter.ifilter;

import com.muzhi.camerasdk.library.utils.MResource;

import android.content.Context;

/**
 */
public class IFHudsonFilter extends IFImageFilter {
    private static final String SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2; //blowout;\n" +
            " uniform sampler2D inputImageTexture3; //overlay;\n" +
            " uniform sampler2D inputImageTexture4; //map\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     \n" +
            "     vec4 texel = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     vec3 bbTexel = texture2D(inputImageTexture2, textureCoordinate).rgb;\n" +
            "     \n" +
            "     texel.r = texture2D(inputImageTexture3, vec2(bbTexel.r, texel.r)).r;\n" +
            "     texel.g = texture2D(inputImageTexture3, vec2(bbTexel.g, texel.g)).g;\n" +
            "     texel.b = texture2D(inputImageTexture3, vec2(bbTexel.b, texel.b)).b;\n" +
            "     \n" +
            "     vec4 mapped;\n" +
            "     mapped.r = texture2D(inputImageTexture4, vec2(texel.r, .16666)).r;\n" +
            "     mapped.g = texture2D(inputImageTexture4, vec2(texel.g, .5)).g;\n" +
            "     mapped.b = texture2D(inputImageTexture4, vec2(texel.b, .83333)).b;\n" +
            "     mapped.a = 1.0;\n" +
            "     gl_FragColor = mapped;\n" +
            " }\n";

    public IFHudsonFilter(Context context) {
        super(context, SHADER);
        setRes(context);
    }

    private void setRes(Context context) {
    	
    	int resId1=MResource.getIdByName(context,MResource.drawable, "hudson_background");
    	int resId2=MResource.getIdByName(context,MResource.drawable, "overlay_map");
    	int resId3=MResource.getIdByName(context,MResource.drawable, "hudson_map");
    	addInputTexture(resId1);
        addInputTexture(resId2);
        addInputTexture(resId3);
        
       /* addInputTexture(R.drawable.hudson_background);
        addInputTexture(R.drawable.overlay_map);
        addInputTexture(R.drawable.hudson_map);*/
    }
}
