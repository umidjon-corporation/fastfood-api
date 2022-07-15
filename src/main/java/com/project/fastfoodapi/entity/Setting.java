package com.project.fastfoodapi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.fastfoodapi.config.settings.*;
import com.project.fastfoodapi.entity.enums.SettingType;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Setting", indexes = {
        @Index(name = "idx_setting_name", columnList = "name")
})
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private Set<String> currentValue;

    @Column(nullable = false)
    private String name;


    public SettingProps getProps(){
        SettingProps props = null;
        for (HumanSetting value : HumanSetting.values()) {
            if(value.name().equalsIgnoreCase(this.name)){
                props=value;
                break;
            }
        }
        if(props==null){
            for (AdminSettings value : AdminSettings.values()) {
                if(value.name().equalsIgnoreCase(this.name)){
                    props=value;
                    break;
                }
            }
        }
        if(props==null){
            for (OperatorSettings value : OperatorSettings.values()) {
                if(value.name().equalsIgnoreCase(this.name)){
                    props=value;
                    break;
                }
            }
        }
        if(props==null){
            for (CourierSettings value : CourierSettings.values()) {
                if(value.name().equalsIgnoreCase(this.name)){
                    props=value;
                    break;
                }
            }
        }

        return props;
    }
}
