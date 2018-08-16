/**
 * Create time: 2018-08-16
 */
package com.lixianling.rabbit.obj;

import com.lixianling.rabbit.DBObject;

/**
 * @author Xianling Li
 */
public class HeroObject extends DBObject {
    private int hp;       // 生命
    private int atk;      // 攻击
    private int def;      // 防御
    private int energy;   // 气力
    private int recover_energy; // 气力恢复速度   // 固定

    private int block_value;          // 格挡
    private int recover_block;  // 格挡值恢复      // 固定

    private int reduce_damage;       // 格挡减伤   百分比  存储*10000
    private int adddam;           // 伤害加成   百分比    存储*10000
    private int addatk;              // 攻击加成    百分比   存储*10000
    private int adddef;              // 防御加成    百分比   存储*10000
    private int reddam;             // 伤害减免
    private int addhp;               // 生命加成    百分比   存储*10000
    private int addcure;            // 医疗加成
    private int stealhp;            // 吸血加成
    private int ignoredef;          // 穿透防御
    private int damagebounce;       // 伤害反弹
    private int extradamage;        // 额外伤害

    private int level;
    private int exp;
    private int modelId;

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getRecover_energy() {
        return recover_energy;
    }

    public void setRecover_energy(int recover_energy) {
        this.recover_energy = recover_energy;
    }

    public int getBlock_value() {
        return block_value;
    }

    public void setBlock_value(int block_value) {
        this.block_value = block_value;
    }

    public int getRecover_block() {
        return recover_block;
    }

    public void setRecover_block(int recover_block) {
        this.recover_block = recover_block;
    }

    public int getReduce_damage() {
        return reduce_damage;
    }

    public void setReduce_damage(int reduce_damage) {
        this.reduce_damage = reduce_damage;
    }

    public int getAdddam() {
        return adddam;
    }

    public void setAdddam(int adddam) {
        this.adddam = adddam;
    }

    public int getAddatk() {
        return addatk;
    }

    public void setAddatk(int addatk) {
        this.addatk = addatk;
    }

    public int getAdddef() {
        return adddef;
    }

    public void setAdddef(int adddef) {
        this.adddef = adddef;
    }

    public int getReddam() {
        return reddam;
    }

    public void setReddam(int reddam) {
        this.reddam = reddam;
    }

    public int getAddhp() {
        return addhp;
    }

    public void setAddhp(int addhp) {
        this.addhp = addhp;
    }

    public int getAddcure() {
        return addcure;
    }

    public void setAddcure(int addcure) {
        this.addcure = addcure;
    }

    public int getStealhp() {
        return stealhp;
    }

    public void setStealhp(int stealhp) {
        this.stealhp = stealhp;
    }

    public int getIgnoredef() {
        return ignoredef;
    }

    public void setIgnoredef(int ignoredef) {
        this.ignoredef = ignoredef;
    }

    public int getDamagebounce() {
        return damagebounce;
    }

    public void setDamagebounce(int damagebounce) {
        this.damagebounce = damagebounce;
    }

    public int getExtradamage() {
        return extradamage;
    }

    public void setExtradamage(int extradamage) {
        this.extradamage = extradamage;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }
}
