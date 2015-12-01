package pers.futuremac.domain;

/**
 * Created by 前程 on 2015/10/22.
 */
public class User {
    public String id;
    public String name;
    public String phone;
    public int status;
    public int companyId;
    public String companyName;
    public String createTime;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
