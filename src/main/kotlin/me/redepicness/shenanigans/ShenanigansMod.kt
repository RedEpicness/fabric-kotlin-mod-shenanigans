package me.redepicness.shenanigans

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.components.inspector.ArrowComponent
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.universal.UMatrixStack
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.item.SpawnEggItem
import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtType

fun init() {
    println("Hello Fabric world!")
}

fun renderCustomCrosshair(matrices: MatrixStack){
    val client = MinecraftClient.getInstance()

    val textureSize = 15

    val xCrosshair = (client.window.scaledWidth - textureSize) / 2
    val yCrosshair = (client.window.scaledHeight - textureSize) / 2

    val entity: Entity? = client.targetedEntity
    val item = SpawnEggItem.forEntity(entity?.type)

    //Draw correct crosshair
    if (item != null) {
        client.itemRenderer.renderGuiItemIcon(matrices, item.defaultStack, xCrosshair, yCrosshair)
    }
    else {
        RenderSystem.blendFuncSeparate(
            GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR,
            GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR,
            GlStateManager.SrcFactor.ONE,
            GlStateManager.DstFactor.ZERO
        )
        InGameHud.drawTexture(matrices, xCrosshair, yCrosshair, 0, 0, textureSize, textureSize)
        RenderSystem.defaultBlendFunc()
    }

    if(entity == null) return

    val window = Window(ElementaVersion.V2)

    val node = makeNbtTree("root", entity.writeNbt(NbtCompound()))
    node.displayComponent.open(true)
    TreeListComponent(listOf(node)).constrain {
        x = 0.pixels()
        y = 0.pixels()
    } childOf window

    window.draw(UMatrixStack(matrices))

}

fun makeNbtTree(name: String, element: NbtElement): TreeNode {
    val node: TreeNode
    when (element) {
        is NbtCompound -> {
            node = NbtNameNode(name, element.nbtType)
            for(key in element.keys){
                element.get(key)?.let { node.addChild(makeNbtTree(key, it)) }
            }
        }
        is AbstractNbtList<*> -> {
            node = NbtNameNode(name, element.nbtType)
            for ((index, el) in element.withIndex()) {
                node.addChild(makeNbtTree(index.toString(), el))
            }
        }
        else -> {
            node = NbtNode(name, element)
        }
    }
    return node
}

class NbtNameNode(private val name: String, private val nbtType: NbtType<*>) : TreeNode() {
    override fun getPrimaryComponent(): UIComponent {
        return UIText("$name ${nbtType.commandFeedbackName}").constrain {
            x = SiblingConstraint()
        }
    }

    override fun getArrowComponent(): TreeArrowComponent {
        return ArrowComponent(false)
    }
}

class NbtNode(private val name: String, private val nbt: NbtElement) : TreeNode() {
    override fun getPrimaryComponent(): UIComponent {
        return UIText(name+": "+nbt.asString()).constrain {
            x = SiblingConstraint()
        }
    }

    override fun getArrowComponent(): TreeArrowComponent {
        return ArrowComponent(true)
    }
}

