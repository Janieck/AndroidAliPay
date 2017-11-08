package baidu.com.androidalipay;

/*
 *  创建者:   Administrator
 *  创建时间:  2017/11/7 23:35
 *  描述：    TODO
 */
public class AlipayInfo {
    private String payType;
    private String errCode;
    private String errMsg;
    private String payInfo;

    public String getPayType() {
        return payType;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
