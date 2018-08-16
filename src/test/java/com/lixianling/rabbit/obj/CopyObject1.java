/**
 * Create time: 2018-08-16
 */
package com.lixianling.rabbit.obj;

import com.lixianling.rabbit.JSONObj;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * @author Xianling Li
 */
public class CopyObject1 extends TestCase {

    public void test_v1() {
        HeroObject hObj = new HeroObject();
        hObj.setHp(1);
        hObj.setAtk(1);
        hObj.setDef(1);
        hObj.setEnergy(1);
        hObj.setRecover_energy(1);
        hObj.setBlock_value(1);
        hObj.setRecover_block(1);
        hObj.setReduce_damage(1);
        hObj.setAddatk(1);
        hObj.setAddcure(1);
        hObj.setAdddam(1);
        hObj.setAdddef(1);
        hObj.setAddhp(1);
        hObj.setStealhp(1);
        hObj.setIgnoredef(1);
        hObj.setDamagebounce(1);
        hObj.setExtradamage(1);
        hObj.setLevel(100);
        hObj.setExp(100);
        hObj.setModelId(100);

        AttrData attrData = new AttrData();

        attrData.hp = 2;
        attrData.atk = 2;
        attrData.def = 2;

        attrData.maxAtk = 2;
        attrData.maxBlock = 2;
        attrData.maxDef = 2;
        Assert.assertEquals(2, attrData.hp);
        Assert.assertEquals(2, attrData.atk);
        Assert.assertEquals(2, attrData.def);
        Assert.assertEquals(0, attrData.energy);
        Assert.assertEquals(0, attrData.recover_energy);
        Assert.assertEquals(0, attrData.block_value);
        Assert.assertEquals(0, attrData.recover_block);
        Assert.assertEquals(0, attrData.reduce_damage);
        Assert.assertEquals(0, attrData.addatk);
        Assert.assertEquals(0, attrData.addcure);
        Assert.assertEquals(0, attrData.adddam);
        Assert.assertEquals(0, attrData.adddef);
        Assert.assertEquals(0, attrData.addhp);
        Assert.assertEquals(0, attrData.stealhp);
        Assert.assertEquals(0, attrData.ignoredef);
        Assert.assertEquals(0, attrData.damagebounce);

        Assert.assertEquals(2, attrData.maxAtk);
        Assert.assertEquals(2, attrData.maxBlock);
        Assert.assertEquals(2, attrData.maxDef);
        attrData.copy(hObj);
        Assert.assertEquals(1, attrData.hp);
        Assert.assertEquals(1, attrData.atk);
        Assert.assertEquals(1, attrData.def);
        Assert.assertEquals(1, attrData.energy);
        Assert.assertEquals(1, attrData.recover_energy);
        Assert.assertEquals(1, attrData.block_value);
        Assert.assertEquals(1, attrData.recover_block);
        Assert.assertEquals(1, attrData.reduce_damage);
        Assert.assertEquals(1, attrData.addatk);
        Assert.assertEquals(1, attrData.addcure);
        Assert.assertEquals(1, attrData.adddam);
        Assert.assertEquals(1, attrData.adddef);
        Assert.assertEquals(1, attrData.addhp);
        Assert.assertEquals(1, attrData.stealhp);
        Assert.assertEquals(1, attrData.ignoredef);
        Assert.assertEquals(1, attrData.damagebounce);

        Assert.assertEquals(2, attrData.maxAtk);
        Assert.assertEquals(2, attrData.maxBlock);
        Assert.assertEquals(2, attrData.maxDef);
        Assert.assertEquals(0, attrData.maxEnergy);
        Assert.assertEquals(0, attrData.maxHp);
        Assert.assertEquals(0, attrData.maxRCBlock);
        Assert.assertEquals(0, attrData.maxRdamge);
        Assert.assertEquals(0, attrData.maxRCEnergy);

        HeroObject hObj2 = JSONObj.newDataObj(HeroObject.class, attrData);

        Assert.assertEquals(1, hObj2.getHp());
        Assert.assertEquals(1, hObj2.getAtk());
        Assert.assertEquals(1, hObj2.getDef());
        Assert.assertEquals(1, hObj2.getEnergy());
        Assert.assertEquals(1, hObj2.getRecover_energy());
        Assert.assertEquals(1, hObj2.getBlock_value());
        Assert.assertEquals(1, hObj2.getRecover_block());
        Assert.assertEquals(1, hObj2.getReduce_damage());
        Assert.assertEquals(1, hObj2.getAddatk());
        Assert.assertEquals(1, hObj2.getAddhp());
        Assert.assertEquals(1, hObj2.getAdddam());
        Assert.assertEquals(1, hObj2.getAdddef());
        Assert.assertEquals(1, hObj2.getAddcure());
        Assert.assertEquals(1, hObj2.getStealhp());
        Assert.assertEquals(1, hObj2.getIgnoredef());
        Assert.assertEquals(1, hObj2.getExtradamage());

        attrData.hp = 2;
        attrData.atk = 2;
        attrData.def = 2;
        attrData.energy = 2;
        attrData.recover_energy = 2;
        attrData.block_value = 2;
        attrData.recover_block = 2;
        attrData.reduce_damage = 2;
        attrData.addatk = 2;
        attrData.addcure = 2;
        attrData.adddam = 2;
        attrData.adddef = 2;
        attrData.addhp = 2;
        attrData.stealhp = 2;
        attrData.ignoredef = 2;
        attrData.damagebounce = 2;
        attrData.extradamage = 2;
        hObj.copy(attrData);
        Assert.assertEquals(2, hObj.getHp());
        Assert.assertEquals(2, hObj.getAtk());
        Assert.assertEquals(2, hObj.getDef());
        Assert.assertEquals(2, hObj.getEnergy());
        Assert.assertEquals(2, hObj.getRecover_energy());
        Assert.assertEquals(2, hObj.getBlock_value());
        Assert.assertEquals(2, hObj.getRecover_block());
        Assert.assertEquals(2, hObj.getReduce_damage());
        Assert.assertEquals(2, hObj.getAddatk());
        Assert.assertEquals(2, hObj.getAddhp());
        Assert.assertEquals(2, hObj.getAdddam());
        Assert.assertEquals(2, hObj.getAdddef());
        Assert.assertEquals(2, hObj.getAddcure());
        Assert.assertEquals(2, hObj.getStealhp());
        Assert.assertEquals(2, hObj.getIgnoredef());
        Assert.assertEquals(2, hObj.getExtradamage());

        Assert.assertEquals(100, hObj.getModelId());
        Assert.assertEquals(100, hObj.getLevel());
        Assert.assertEquals(100, hObj.getExp());

    }
}
