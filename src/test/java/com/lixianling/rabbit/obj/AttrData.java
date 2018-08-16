/**
 * Create time: 2018-08-16
 */
package com.lixianling.rabbit.obj;

import com.lixianling.rabbit.JSONObj;

/**
 * @author Xianling Li
 */
public class AttrData extends JSONObj {
    /**
     * 三个当前状态（表现层）
     */
    public int currentHp;
    public int currentBlock;
    public int currentEnergy;


    /**
     * 属性
     */
    public int hp;
    public int atk;
    public int def;
    public int energy;
    public int recover_energy;

    public int block_value;
    public int recover_block;

    public int reduce_damage;

    public int adddam;
    public int addatk;
    public int adddef;
    public int reddam;             // 伤害减免
    public int addcure;            // 医疗加成
    public int addhp = 0;          // 生命加成
    public int stealhp;            // 吸血加成
    public int ignoredef;          // 穿透防御
    public int damagebounce;       // 伤害反弹
    public int extradamage;        // 额外伤害

    // 计算百分比需要
    public int maxHp = 0;
    public int maxAtk = 0;
    public int maxDef = 0;
    public int maxBlock = 0;
    public int maxEnergy = 0;
    public int maxRdamge = 0;
    public int maxRCEnergy = 0;
    public int maxRCBlock = 0;

    /**
     * BUFF 临时属性值
     */
    public int buffReduceDam;  // 减伤
    public int bufAddHp;       // 护盾
    public int buffAddDam;
}
