package com.alex.mcgp.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author majf
 * @since 2021-02-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Dim201Org5mmf对象", description="")
@AllArgsConstructor
@NoArgsConstructor
public class Dim201Org5mmf implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "标准字段_代理键")
    private String sKey;

    @ApiModelProperty(value = "组织机构_编号")
    private String orgCode;

    @ApiModelProperty(value = "组织机构_编号")
    private String orgCodeNew;

    @ApiModelProperty(value = "组织机构_名称")
    private String fullName;

    @ApiModelProperty(value = "组织机构_简称")
    private String shortName;

    @ApiModelProperty(value = "组织机构_上级机构id")
    private String parentId;

    @ApiModelProperty(value = "组织机构_所属水司id")
    private String companyId;

    @ApiModelProperty(value = "组织机构_所属集团id")
    private String groupId;

    @ApiModelProperty(value = "数据字典_机构类型(dc201_org_type)")
    private String orgType;

    @ApiModelProperty(value = "组织机构_机构地址编码")
    private String addressCode;

    @ApiModelProperty(value = "组织机构_机构地址详情")
    private String addressInfo;

    @ApiModelProperty(value = "组织机构_经度")
    private String longitude;

    @ApiModelProperty(value = "组织机构_纬度")
    private String latitude;

    @ApiModelProperty(value = "组织机构_负责人")
    private String charger;

    @ApiModelProperty(value = "组织机构_联系电话")
    private String tel;

    @ApiModelProperty(value = "组织机构_传真")
    private String fax;

    @ApiModelProperty(value = "组织机构_成立年月")
    private String buildDate;

    @ApiModelProperty(value = "数据字典_企业级别(dc201_enterprise_level)")
    private String enterpriseLevel;

    @ApiModelProperty(value = "数据字典_数据来源(dc201_data_source)")
    private String source;

    @ApiModelProperty(value = "组织机构_创建时间")
    private String createTime;

    @ApiModelProperty(value = "组织机构_修改时间")
    private String updateTime;

    @ApiModelProperty(value = "数据字典_有效状态(dc1201_valid)")
    private String isValid;

    @ApiModelProperty(value = "标准字段_入库时间")
    private String sSdt;

    @ApiModelProperty(value = "标准字段_源业务系统组织机构")
    private String sOrgCode;

    @ApiModelProperty(value = "标准字段_源业务系统schema")
    private String sSchema;

    @ApiModelProperty(value = "标准字段_拉链开始时间")
    private String sStartTime;

    @ApiModelProperty(value = "标准字段_拉链结束时间")
    private String sEndTime;

    @ApiModelProperty(value = "标准字段_数据是否有效")
    private String sStat;

    @ApiModelProperty(value = "标准字段_数据来源")
    private String sSrc;


}
