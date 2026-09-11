package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.isActive

internal fun Modifier.characterReorderSessionViewportV4(
    vararg sessions: CharacterReorderSessionV4,
): Modifier = onGloballyPositioned { coordinates ->
    val bounds = coordinates.boundsInRoot()
    sessions.forEach { it.setViewport(bounds) }
}

/** Keep on the complete card/item whose rendered slot participates in destination geometry. */
@Composable
internal fun Modifier.characterReorderSessionBoundsV4(
    session: CharacterReorderSessionV4,
    id: String,
): Modifier {
    DisposableEffect(session, id) {
        onDispose { session.unregisterBounds(id) }
    }
    return onGloballyPositioned { session.registerBounds(id, it.boundsInRoot()) }
}

/**
 * Long-press pickup surface. Put this on the non-interactive card body, not around nested buttons,
 * checkboxes, menus, quick-use controls, or other child actions.
 */
@Composable
internal fun Modifier.characterReorderSessionDragHandleV4(
    session: CharacterReorderSessionV4,
    id: String,
): Modifier {
    var handleBounds by remember(session, id) { mutableStateOf<Rect?>(null) }
    val geometry = onGloballyPositioned { handleBounds = it.boundsInRoot() }
    if (!session.enabled) return geometry

    return geometry.pointerInput(session, id, session.enabled) {
        var pickupAccepted = false
        detectDragGesturesAfterLongPress(
            onDragStart = { localOffset ->
                val rootBounds = handleBounds
                pickupAccepted = if (rootBounds == null) {
                    false
                } else {
                    session.beginDragAtRoot(id, rootBounds.topLeft + localOffset)
                }
            },
            onDrag = { change, amount ->
                if (pickupAccepted) {
                    change.consume()
                    session.dragBy(amount)
                }
            },
            onDragEnd = {
                if (pickupAccepted) session.finishDrag()
                pickupAccepted = false
            },
            onDragCancel = {
                if (pickupAccepted) session.cancelDrag()
                pickupAccepted = false
            },
        )
    }
}

/** Accessibility/switch-control equivalent of direct drag. */
@Composable
internal fun Modifier.characterReorderSessionSemanticsV4(
    session: CharacterReorderSessionV4,
    id: String,
): Modifier {
    if (!session.enabled) return this
    return semantics {
        customActions = listOf(
            CustomAccessibilityAction(label = "Mover antes") {
                session.semanticMove(id = id, delta = -1)
            },
            CustomAccessibilityAction(label = "Mover después") {
                session.semanticMove(id = id, delta = 1)
            },
        )
    }
}

/** Convenience for simple rows whose complete surface is safe as the long-press pickup target. */
@Composable
internal fun Modifier.characterReorderSessionItemV4(
    session: CharacterReorderSessionV4,
    id: String,
): Modifier = this
    .characterReorderSessionBoundsV4(session, id)
    .characterReorderSessionDragHandleV4(session, id)
    .characterReorderSessionSemanticsV4(session, id)

/**
 * Bounded/non-lazy visual adapter. Large lazy collections use an independent lifted overlay so the
 * dragged visual remains composed after its placeholder scrolls outside the lazy viewport.
 */
@Composable
internal fun Modifier.characterReorderSessionVisualV4(
    session: CharacterReorderSessionV4,
    id: String,
): Modifier {
    val active = session.draggedId == id
    val scale = animateFloatAsState(
        targetValue = if (active) 1.035f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "reorder-session-pickup-scale",
    ).value
    val elevation = animateFloatAsState(
        targetValue = if (active) 18f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "reorder-session-pickup-elevation",
    ).value
    val translation = if (active) session.draggedTranslation(id) else androidx.compose.ui.geometry.Offset.Zero

    return zIndex(if (active) 50f else 0f).graphicsLayer {
        translationX = translation.x
        translationY = translation.y
        scaleX = scale
        scaleY = scale
        shadowElevation = elevation.dp.toPx()
    }
}

@Composable
internal fun CharacterReorderSessionAutoScrollEffectV4(
    session: CharacterReorderSessionV4,
) {
    val density = LocalDensity.current.density
    LaunchedEffect(session, session.active) {
        if (!session.active) return@LaunchedEffect
        while (isActive && session.active) {
            androidx.compose.runtime.withFrameNanos { }
            session.autoScrollOneFrame(density)
        }
    }
}
