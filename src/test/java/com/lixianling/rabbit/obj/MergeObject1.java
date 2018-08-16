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
public class MergeObject1 extends TestCase {

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
        attrData.merge(hObj);
        Assert.assertEquals(2, attrData.hp);
        Assert.assertEquals(2, attrData.atk);
        Assert.assertEquals(2, attrData.def);
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

    }
}
