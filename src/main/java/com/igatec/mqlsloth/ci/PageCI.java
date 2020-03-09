package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.annotation.ModStringProvider;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;

import java.util.Map;

public class PageCI extends AdminObjectCI {

    private String content;
    private String mime;

    public PageCI(String name) {
        this(name, CIDiffMode.TARGET);
    }
    public PageCI(String name, CIDiffMode diffMode) {
        super(SlothAdminType.PAGE, name, diffMode);
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }
    private void initTarget(){
        content = "";
        mime = "";
    }
    private void initDiff(){
        content = null;
        mime = null;
    }

    @ModStringProvider(M_CONTENT)
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        checkModeAssertion(content != null, CIDiffMode.TARGET);
        this.content = content;
    }


    @ModStringProvider(M_MIME)
    public String getMime() {
        return mime;
    }
    public void setMime(String mime) {
        checkModeAssertion(mime != null, CIDiffMode.TARGET);
        this.mime = mime;
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        PageCI newCastedCI = (PageCI) newCI;
        PageCI diffCastedCI = (PageCI) diffCI;
        {
            String value = newCastedCI.getMime();
            if (value != null && !value.equals(getMime())){
                diffCastedCI.setMime(value);
            }
        }
        {
            String value = newCastedCI.getContent();
            if (value != null && !value.equals(getContent())){
                diffCastedCI.setContent(value);
            }
        }
    }

    @Override
    public boolean isEmpty(){
        if (!super.isEmpty()) return false;
        return mime==null && content==null;
    }

    @Override
    public PageCI buildDiff(AbstractCI newCI) {
        PageCI ci = (PageCI) newCI;
        PageCI diff = new PageCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public AbstractCI buildDefaultCI(){
        return new PageCI(getName());
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> fieldsValues = super.toMap();
        fieldsValues.put(Y_MIME, getMime());
        fieldsValues.put(Y_CONTENT, getContent());

        return fieldsValues;
    }
}
