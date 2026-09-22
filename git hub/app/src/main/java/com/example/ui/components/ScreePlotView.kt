package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PcaResult
import com.example.ui.theme.BarNormalColor
import com.example.ui.theme.BarSelectedColor
import com.example.ui.theme.CumulativeCurveColor
import com.example.ui.theme.ElbowColor
import com.example.ui.theme.KaiserLineColor
import java.util.Locale

@Composable
fun ScreePlotView(
    pcaResult: PcaResult,
    modifier: Modifier = Modifier
) {
    var selectedK by remember { mutableIntStateOf(2) } // Valor recomendado por el Codo
    var showCumulative by remember { mutableStateOf(true) }
    var showKaiserLine by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("scree_plot_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Encabezado pedagógico
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = "Gráfico del Codo",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Método del Codo (Scree Plot)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Criterio visual y matemático para decidir cuántos componentes conservar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Gráfico Interactivo Canvas del Codo
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Controles rápidos de capas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Varianza Explicada vs Componente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = showCumulative,
                                onClick = { showCumulative = !showCumulative },
                                label = { Text("% Acumulado", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = showKaiserLine,
                                onClick = { showKaiserLine = !showKaiserLine },
                                label = { Text("Kaiser (λ=1)", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lienzo del gráfico
                    InteractiveScreeCanvas(
                        pcaResult = pcaResult,
                        selectedK = selectedK,
                        showCumulative = showCumulative,
                        showKaiserLine = showKaiserLine,
                        onSelectK = { selectedK = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Leyenda del gráfico
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem(color = BarNormalColor, text = "Varianza PC")
                        LegendItem(color = ElbowColor, text = "Punto del Codo (Elbow)")
                        if (showCumulative) {
                            LegendItem(color = CumulativeCurveColor, text = "Acumulada %")
                        }
                        if (showKaiserLine) {
                            LegendItem(color = KaiserLineColor, text = "Límite Kaiser (λ≥1)")
                        }
                    }
                }
            }
        }

        // Selector Interactivo de K componentes con Slider
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Simular Conservación de Componentes:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedK == 2) ElbowColor else MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = "k = $selectedK de 6",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = selectedK.toFloat(),
                        onValueChange = { selectedK = it.toInt() },
                        valueRange = 1f..6f,
                        steps = 4,
                        modifier = Modifier.testTag("k_components_slider")
                    )

                    // Fila de botones para seleccionar directamente
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (k in 1..6) {
                            val isElbow = (k == 2)
                            val isSelected = (k == selectedK)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            isElbow -> ElbowColor.copy(alpha = 0.2f)
                                            else -> MaterialTheme.colorScheme.surface
                                        },
                                        CircleShape
                                    )
                                    .border(
                                        width = if (isElbow) 2.dp else 1.dp,
                                        color = if (isElbow) ElbowColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .clickable { selectedK = k },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "PC$k",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected || isElbow) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Panel de Métricas de Decisión para el valor k seleccionado
        item {
            val cumVar = pcaResult.cumulativeVariancePct[selectedK - 1]
            val discardedVar = 100.0 - cumVar
            val dimReduction = ((6 - selectedK).toDouble() / 6.0) * 100.0

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Balance Información vs Dimensionalidad con k = $selectedK",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricBox(
                            title = "Varianza Retenida",
                            value = String.format(Locale.US, "%.1f%%", cumVar),
                            subtitle = "Señal preservada",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "Compresión",
                            value = String.format(Locale.US, "%.0f%%", dimReduction),
                            subtitle = "De 6D a ${selectedK}D",
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "Ruido Descartado",
                            value = String.format(Locale.US, "%.1f%%", discardedVar),
                            subtitle = "Pérdida admisible",
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Diagnóstico pedagógico del valor k seleccionado
                    val (verdictTitle, verdictDesc, verdictIcon) = when (selectedK) {
                        1 -> Triple(
                            "k=1: Subajuste (Underfitting)",
                            "Retener solo PC1 preserva el 53.3% de la información pero ignora por completo la mortalidad infantil y las variaciones no agrarias.",
                            Icons.Default.Info
                        )
                        2 -> Triple(
                            "k=2: ¡El Punto Óptimo del Codo!",
                            "Con solo 2 componentes (PC1 y PC2) conservamos más del 73.1% de toda la información. Cumple estrictamente el Criterio de Kaiser (λ ≥ 1) y permite graficar en 2D sin perder la estructura real.",
                            Icons.Default.CheckCircle
                        )
                        3 -> Triple(
                            "k=3: Alternativa Válida (87.3% varianza)",
                            "PC3 añade un 14.1% adicional (capturando principalmente la división religiosa católica/protestante pura). Aunque su autovalor (0.85) es menor que 1, es útil si se requiere alta precisión.",
                            Icons.Default.Lightbulb
                        )
                        else -> Triple(
                            "k=$selectedK: Rendimientos Decrecientes",
                            "Añadir los componentes 4, 5 o 6 aporta menos del 7% cada uno. Se entra de lleno en la zona de 'escombros' (scree) donde se modela más ruido aleatorio que estructura real.",
                            Icons.Default.CompareArrows
                        )
                    }

                    Surface(
                        color = if (selectedK == 2) ElbowColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = verdictIcon,
                                contentDescription = null,
                                tint = if (selectedK == 2) ElbowColor else MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = verdictTitle,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedK == 2) ElbowColor else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = verdictDesc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Explicación profunda del Codo y Cattell
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¿Por qué se llama 'Gráfico del Codo' o 'Scree Plot'?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "El término 'Scree Test' fue propuesto en 1966 por Raymond Cattell utilizando una brillante metáfora geológica:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PedagogyConceptCard(
                        title = "1. El Acantilado (Cliff)",
                        desc = "Los primeros componentes (PC1 y PC2) descienden de forma pronunciada. Representan los pilares macizos donde reside la señal genuina de los datos.",
                        accent = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    PedagogyConceptCard(
                        title = "2. El Codo (Elbow Point)",
                        desc = "El punto de inflexión donde la pendiente vertical se frena en seco y cambia a una línea casi horizontal. En el dataset Swiss, ocurre exactamente en k = 2.",
                        accent = ElbowColor
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    PedagogyConceptCard(
                        title = "3. Los Escombros (Scree)",
                        desc = "En geología, 'scree' son los fragmentos de roca desprendidos que reposan al pie de una montaña. En PCA, a partir de PC3 o PC4 sólo quedan pequeñas fluctuaciones estadísticas y ruido.",
                        accent = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // Los 3 Criterios de Selección Estándar en Estadística
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Los 3 Criterios de Elección en el Dataset Swiss",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    CriterionRow(
                        name = "Criterio 1: Regla del Codo (Cattell)",
                        rule = "Parar antes de que la curva se nivele",
                        resultSwiss = "Selecciona k = 2 (inflexión clara)",
                        statusColor = ElbowColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CriterionRow(
                        name = "Criterio 2: Regla de Kaiser (λ ≥ 1.0)",
                        rule = "Conservar autovalores mayores a la media (1.0)",
                        resultSwiss = "λ₁=3.20 (≥1), λ₂=1.19 (≥1), λ₃=0.85 (<1). Retiene k = 2.",
                        statusColor = KaiserLineColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CriterionRow(
                        name = "Criterio 3: Varianza Acumulada (70-80%)",
                        rule = "Superar el umbral del 70% o 75% de variabilidad",
                        resultSwiss = "PC1 + PC2 alcanzan el 73.13% de la varianza total.",
                        statusColor = CumulativeCurveColor
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveScreeCanvas(
    pcaResult: PcaResult,
    selectedK: Int,
    showCumulative: Boolean,
    showKaiserLine: Boolean,
    onSelectK: (Int) -> Unit
) {
    val eigvals = pcaResult.eigenvalues
    val varPct = pcaResult.varianceExplainedPct
    val cumPct = pcaResult.cumulativeVariancePct
    val p = eigvals.size

    val maxVarPct = 60f // Escala eje Y hasta 60%
    val maxEigval = 4.0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val width = size.width
                    val leftPadding = 50f
                    val rightPadding = 50f
                    val plotWidth = width - leftPadding - rightPadding
                    val slotWidth = plotWidth / p
                    val tappedX = offset.x - leftPadding
                    if (tappedX in 0f..plotWidth) {
                        val index = (tappedX / slotWidth).toInt().coerceIn(0, p - 1)
                        onSelectK(index + 1)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val padLeft = 46f
            val padRight = 46f
            val padTop = 36f
            val padBottom = 42f

            val plotW = w - padLeft - padRight
            val plotH = h - padTop - padBottom

            // 1. Líneas de rejilla horizontales (eje % y autovalor)
            val gridSteps = 4
            for (i in 0..gridSteps) {
                val fraction = i.toFloat() / gridSteps
                val y = padTop + plotH * (1f - fraction)
                val pctValue = (fraction * maxVarPct).toInt()

                drawLine(
                    color = Color.Gray.copy(alpha = 0.2f),
                    start = Offset(padLeft, y),
                    end = Offset(w - padRight, y),
                    strokeWidth = 1f
                )

                // Texto eje Y izquierdo (% varianza)
                drawContext.canvas.nativeCanvas.drawText(
                    "$pctValue%",
                    padLeft - 8f,
                    y + 4f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 22f
                        textAlign = android.graphics.Paint.Align.RIGHT
                        isAntiAlias = true
                    }
                )
            }

            // 2. Línea de Kaiser (autovalor = 1.0 -> 100/6 = 16.67% de varianza)
            if (showKaiserLine) {
                val kaiserPct = (1.0f / 6.0f) * 100f // 16.67%
                val kaiserY = padTop + plotH * (1f - (kaiserPct / maxVarPct))

                drawLine(
                    color = KaiserLineColor.copy(alpha = 0.85f),
                    start = Offset(padLeft, kaiserY),
                    end = Offset(w - padRight, kaiserY),
                    strokeWidth = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )

                drawContext.canvas.nativeCanvas.drawText(
                    "λ = 1.0 (Kaiser)",
                    w - padRight - 4f,
                    kaiserY - 6f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.RED
                        textSize = 20f
                        isFakeBoldText = true
                        textAlign = android.graphics.Paint.Align.RIGHT
                        isAntiAlias = true
                    }
                )
            }

            val slotWidth = plotW / p
            val barWidth = slotWidth * 0.52f
            val nodePoints = mutableListOf<Offset>()
            val cumPoints = mutableListOf<Offset>()

            // 3. Dibujar Barras de Varianza Individual
            for (i in 0 until p) {
                val centerX = padLeft + i * slotWidth + slotWidth / 2f
                val pct = varPct[i].toFloat()
                val barH = (pct / maxVarPct) * plotH
                val topY = padTop + plotH - barH

                val isElbow = (i == 1) // PC2 es el codo
                val isSelected = (i + 1 == selectedK)

                val barColor = when {
                    isSelected -> BarSelectedColor
                    isElbow -> ElbowColor
                    else -> BarNormalColor
                }

                // Barra con esquinas redondeadas arriba
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(centerX - barWidth / 2f, topY),
                    size = Size(barWidth, barH),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Etiqueta de valor sobre la barra
                drawContext.canvas.nativeCanvas.drawText(
                    String.format(Locale.US, "%.1f%%", pct),
                    centerX,
                    topY - 8f,
                    android.graphics.Paint().apply {
                        color = if (isElbow) android.graphics.Color.parseColor("#EA580C") else android.graphics.Color.DKGRAY
                        textSize = 21f
                        isFakeBoldText = isElbow || isSelected
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                )

                // Etiqueta X: PC1..PC6
                drawContext.canvas.nativeCanvas.drawText(
                    "PC${i + 1}",
                    centerX,
                    padTop + plotH + 26f,
                    android.graphics.Paint().apply {
                        color = if (isSelected) android.graphics.Color.BLACK else android.graphics.Color.GRAY
                        textSize = 22f
                        isFakeBoldText = isSelected
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                )

                // Autovalor debajo
                drawContext.canvas.nativeCanvas.drawText(
                    String.format(Locale.US, "λ=%.2f", eigvals[i]),
                    centerX,
                    padTop + plotH + 46f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 17f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                )

                nodePoints.add(Offset(centerX, topY))

                // Punto curva acumulada
                val cum = cumPct[i].toFloat()
                val cumY = padTop + plotH * (1f - (cum / 100f))
                cumPoints.add(Offset(centerX, cumY))
            }

            // 4. Conectar línea del Scree Plot (Curva de Autovalores)
            val screePath = Path()
            nodePoints.forEachIndexed { idx, pt ->
                if (idx == 0) screePath.moveTo(pt.x, pt.y) else screePath.lineTo(pt.x, pt.y)
            }
            drawPath(
                path = screePath,
                color = Color(0xFF1E293B),
                style = Stroke(width = 3.5f)
            )

            // Nodos circulares del Scree
            nodePoints.forEachIndexed { idx, pt ->
                val isElbow = (idx == 1)
                drawCircle(
                    color = if (isElbow) ElbowColor else Color(0xFF1E293B),
                    radius = if (isElbow) 7f else 4.5f,
                    center = pt
                )
                if (isElbow) {
                    drawCircle(
                        color = Color.White,
                        radius = 3.5f,
                        center = pt
                    )
                }
            }

            // 5. Curva de Varianza Acumulada (Línea Azul)
            if (showCumulative) {
                val cumPath = Path()
                cumPoints.forEachIndexed { idx, pt ->
                    if (idx == 0) cumPath.moveTo(pt.x, pt.y) else cumPath.lineTo(pt.x, pt.y)
                }
                drawPath(
                    path = cumPath,
                    color = CumulativeCurveColor,
                    style = Stroke(width = 2.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f))
                )

                cumPoints.forEachIndexed { idx, pt ->
                    drawCircle(
                        color = CumulativeCurveColor,
                        radius = 3.5f,
                        center = pt
                    )
                    // Mostrar % acumulado sobre el punto
                    drawContext.canvas.nativeCanvas.drawText(
                        String.format(Locale.US, "%.0f%%", cumPct[idx]),
                        pt.x,
                        pt.y - 8f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#2563EB")
                            textSize = 19f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                        }
                    )
                }
            }

            // 6. Resaltado especial visual y llamada: "EL CODO (ELBOW)"
            val elbowPt = nodePoints[1] // PC2
            // Anillo exterior brillante
            drawCircle(
                color = ElbowColor.copy(alpha = 0.35f),
                radius = 16f,
                center = elbowPt
            )
            drawCircle(
                color = ElbowColor,
                radius = 7.5f,
                center = elbowPt,
                style = Stroke(width = 2.5f)
            )

            // Flecha indicadora y etiqueta "Punto del Codo"
            val calloutX = elbowPt.x + 30f
            val calloutY = elbowPt.y - 28f
            drawLine(
                color = ElbowColor,
                start = Offset(calloutX, calloutY),
                end = Offset(elbowPt.x + 8f, elbowPt.y - 8f),
                strokeWidth = 2f
            )

            drawRoundRect(
                color = ElbowColor,
                topLeft = Offset(calloutX, calloutY - 20f),
                size = Size(116f, 26f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawContext.canvas.nativeCanvas.drawText(
                "EL CODO (k=2)",
                calloutX + 58f,
                calloutY - 3f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 17f
                    isFakeBoldText = true
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MetricBox(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = color,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PedagogyConceptCard(
    title: String,
    desc: String,
    accent: Color
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .padding(top = 4.dp)
                    .background(accent, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun CriterionRow(
    name: String,
    rule: String,
    resultSwiss: String,
    statusColor: Color
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
                Badge(
                    containerColor = statusColor.copy(alpha = 0.15f),
                    contentColor = statusColor
                ) {
                    Text("Recomienda k=2", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Regla: $rule",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "En Swiss: $resultSwiss",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
