package dev.techtrain

import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

@OptIn(DeprecatedForRemovalCompilerApi::class)
class ComposableModifierSemanticsTransformer(
    private val pluginContext: IrPluginContext,
    private val logger: Logger
) : IrElementTransformerVoid() {

    private val semanticsFunction: IrSimpleFunctionSymbol = pluginContext.referenceFunctions(
        CallableId(
            packageName = FqName("com.example.techtrain.railway.android"),
            callableName = Name.identifier("autoSemantics")
        )
    ).first()
    private val semantics2Function: IrSimpleFunctionSymbol = pluginContext.referenceFunctions(
        CallableId(
            packageName = FqName("com.example.techtrain.railway.android"),
            callableName = Name.identifier("newAutoSemantics")
        )
    ).first()

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitCall(expression: IrCall): IrExpression {
        val transformedExpression = super.visitCall(expression)
        if (transformedExpression !is IrCall) {
            return transformedExpression
        }
        if (!transformedExpression.isComposableFun()) {
            return transformedExpression
        }

        val modifierParameter = transformedExpression.symbol.owner.valueParameters
            .firstOrNull { it.type.classFqName == MODIFIER_FQ_NAME }
            ?: return transformedExpression
        logger.log("${transformedExpression.symbol.owner.name} ${transformedExpression.valueArgumentsCount}")

        if (transformedExpression.symbol.owner.name.asString() == "Image") {
            val painterParameter = transformedExpression.symbol.owner.valueParameters
                .firstOrNull { it.type.classFqName == PAINTER_FQ_NAME }
            if (painterParameter != null) {
                logger.log("    -> Found Painter parameter: ${painterParameter.name}")
                val painterArgument = transformedExpression.getValueArgument(painterParameter.index)
                if (painterArgument == null) {
                    logger.log("    -> No Painter argument found")
                } else {
                    logger.log("    -> Painter argument found: ${painterArgument}")
                }
            }
        }

        val modifierArgument = transformedExpression.getValueArgument(modifierParameter.index)
        if (modifierArgument == null) {
            logger.log("    -> No Modifier argument found")
            val ab = with(pluginContext.irBuiltIns.createIrBuilder(transformedExpression.symbol)) {
                irCall(semantics2Function).apply {
                    putValueArgument(
                        0,
                        irString(transformedExpression.symbol.owner.name.asString())
                    )
                }
            }
            transformedExpression.putValueArgument(modifierParameter.index, ab)

            return transformedExpression
        }

        logger.log("    -> Modifier argument found: ${modifierArgument}")

        when (modifierArgument) {
            is IrCall -> {
                val a = modifierArgument.transform(object : IrElementTransformerVoid() {
                    override fun visitCall(expression: IrCall): IrExpression {
                        logger.log("    -> ${semanticsFunction.owner.name} ${expression.valueArgumentsCount}")
                        return with(pluginContext.irBuiltIns.createIrBuilder(expression.symbol)) {
                            irCall(semanticsFunction).apply {
                                extensionReceiver = expression
                                putValueArgument(
                                    0,
                                    irString(transformedExpression.symbol.owner.name.asString())
                                )
                            }
                        }
                    }
                }, null)
                transformedExpression.putValueArgument(modifierParameter.index, a)
            }

            is IrGetValue -> {
                val a = modifierArgument.transform(object : IrElementTransformerVoid() {
                    override fun visitGetValue(expression: IrGetValue): IrExpression {
                        logger.log("    -> ${semanticsFunction.owner.name}")
                        return with(pluginContext.irBuiltIns.createIrBuilder(expression.symbol)) {
                            irCall(semanticsFunction).apply {
                                extensionReceiver = expression
                                putValueArgument(
                                    0,
                                    irString(transformedExpression.symbol.owner.name.asString())
                                )
                            }
                        }
                    }
                }, null)
                transformedExpression.putValueArgument(modifierParameter.index, a)
            }

            else -> {}
        }
        return transformedExpression
    }

    @UnsafeDuringIrConstructionAPI
    private fun IrCall.isComposableFun(): Boolean =
        symbol.owner.annotations.any { it.type.classFqName == COMPOSABLE_ANNOTATION_FQ_NAME }

    companion object {
        private val COMPOSABLE_ANNOTATION_FQ_NAME = FqName("androidx.compose.runtime.Composable")
        private val MODIFIER_FQ_NAME = FqName("androidx.compose.ui.Modifier")
        private val PAINTER_FQ_NAME = FqName("androidx.compose.ui.graphics.painter.Painter")
    }
}
