package me.Thelnfamous1.betterhorsejumping.client;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class ContainedHierarchicalModel<E extends Entity> extends HierarchicalModel<E> {

    private final ModelPart root;

    public ContainedHierarchicalModel(ModelPart root){
        this.root = root;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(E pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

    }
}
