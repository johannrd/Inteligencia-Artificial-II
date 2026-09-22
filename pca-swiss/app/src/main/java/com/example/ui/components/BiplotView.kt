package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ScatterPlot
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PcaResult
import com.example.model.ProjectedProvince
import com.example.model.SWISS_VARIABLES
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BiplotView(
    pcaResult: PcaResult,
    modifier: Modifier = Modifier
) {
    var selectedProvince by remember { mutableStateOf<ProjectedProvince?>(null) }
    var colorByCatholic by remember { mutableStateOf(true) }
    var showVectors by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("biplot_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Encabezado
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.secondary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ScatterPlot,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Biplot 2D: Proyección PC1 vs PC2",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Muestra simultáneamente las 47 provincias y las 6 variables",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Lienzo del Biplot
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Controles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Plano Principal (73.1% var)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = showVectors,
                                onClick = { showVectors = !showVectors },
                                label = { Text("Vectores (6)", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = colorByCatholic,
                                onClick = { colorByCatholic = !colorByCatholic },
                                label = { Text(if (colorByCatholic) "Color: Religión" else "Color: Monocromo", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    BiplotCanvas(
                        pcaResult = pcaResult,
                        selectedProvince = selectedProvince,
                        colorByCatholic = colorByCatholic,
                        showVectors = showVectors,
                        onSelectProvince = { selectedProvince = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Leyenda
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (colorByCatholic) {
                            LegendItem(color = Color(0xFFD97706), text = "Católico (>50%)")
                            LegendItem(color = Color(0xFF0F766E), text = "Protestante (<50%)")
                        } else {
                            LegendItem(color = MaterialTheme.colorScheme.primary, text = "47 Provincias")
                        }
                        if (showVectors) {
                            LegendItem(color = Color(0xFFDC2626), text = "Vectores de Variables")
                        }
                    }
                }
            }
        }

        // Tarjeta de Detalle de la Provincia Seleccionada
        item {
            AnimatedVisibility(visible = selectedProvince != null) {
                selectedProvince?.let { proj ->
                    val prov = proj.province
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = prov.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                IconButton(onClick = { selectedProvince = null }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Coordenadas PC
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Score PC1", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                        Text(String.format(Locale.US, "%.3f", proj.scores[0]), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Score PC2", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.secondary)
                                        Text(String.format(Locale.US, "%.3f", proj.scores[1]), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Valores Históricos Originales (1888):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            // Grid de valores
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                DataValueRow("Fertilidad", "${prov.fertility} Ig")
                                DataValueRow("Agricultura", "${prov.agriculture}%")
                                DataValueRow("Examen Militar", "${prov.examination}% alta nota")
                                DataValueRow("Educación", "${prov.education}% secundaria")
                                DataValueRow("Población Católica", "${prov.catholic}%")
                                DataValueRow("Mortalidad Infantil", "${prov.infantMortality}%")
                            }
                        }
                    }
                }
            }
        }

        // Guía pedagógica de lectura del Biplot
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "¿Cómo interpretar el Biplot?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "1. Dirección de los Vectores (Flechas):\n" +
                                "• El ángulo entre dos vectores indica su correlación: ángulos agudos (<90°) indican correlación positiva (Educación y Examen Militar apuntan juntos). Vectores opuestos (~180°) indican correlación negativa (Agricultura vs Examen).\n\n" +
                                "2. Longitud de los Vectores:\n" +
                                "• Cuanto más largo sea el vector rojo, mejor representado está en las 2 primeras dimensiones.\n\n" +
                                "3. Posición de las Provincias (Puntos):\n" +
                                "• Provincias cercanas entre sí tienen perfiles socioeconómicos muy similares en 1888.\n" +
                                "• Si proyectas un punto sobre la flecha de una variable, obtienes el valor aproximado de esa provincia en dicha variable.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DataValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun BiplotCanvas(
    pcaResult: PcaResult,
    selectedProvince: ProjectedProvince?,
    colorByCatholic: Boolean,
    showVectors: Boolean,
    onSelectProvince: (ProjectedProvince) -> Unit
) {
    val provinces = pcaResult.projectedProvinces
    val loadings = pcaResult.variableLoadings

    // Rango de scores PC1 y PC2: típicamente [-3.5 .. +3.5]
    val maxRange = 3.6f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .pointerInput(provinces) {
                detectTapGestures { tapOffset ->
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f
                    val scaleX = (w / 2f - 30f) / maxRange
                    val scaleY = (h / 2f - 30f) / maxRange

                    var closest: ProjectedProvince? = null
                    var minDist = 45f // Radio táctil en px

                    provinces.forEach { proj ->
                        val px = cx + (proj.scores[0].toFloat() * scaleX)
                        val py = cy - (proj.scores[1].toFloat() * scaleY) // Invertir Y
                        val dist = kotlin.math.hypot(tapOffset.x - px, tapOffset.y - py)
                        if (dist < minDist) {
                            minDist = dist
                            closest = proj
                        }
                    }
                    if (closest != null) {
                        onSelectProvince(closest!!)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            val scaleX = (w / 2f - 24f) / maxRange
            val scaleY = (h / 2f - 24f) / maxRange

            // 1. Ejes cartesianos (PC1 = horizontal, PC2 = vertical)
            drawLine(
                color = Color.LightGray,
                start = Offset(16f, cy),
                end = Offset(w - 16f, cy),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )
            drawLine(
                color = Color.LightGray,
                start = Offset(cx, 16f),
                end = Offset(cx, h - 16f),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // Etiquetas de los ejes
            drawContext.canvas.nativeCanvas.drawText(
                "PC1 (53.3% var) →",
                w - 24f,
                cy - 8f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 21f
                    isFakeBoldText = true
                    textAlign = android.graphics.Paint.Align.RIGHT
                    isAntiAlias = true
                }
            )

            drawContext.canvas.nativeCanvas.drawText(
                "↑ PC2 (19.8% var)",
                cx + 8f,
                26f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 21f
                    isFakeBoldText = true
                    textAlign = android.graphics.Paint.Align.LEFT
                    isAntiAlias = true
                }
            )

            // 2. Dibujar Vectores de Variables (Flechas Rojas)
            if (showVectors) {
                val vectorScale = 2.4f // Escalar para visualización en el mismo plano
                loadings.forEach { vl ->
                    val lx = vl.loadings[0].toFloat() * vectorScale
                    val ly = vl.loadings[1].toFloat() * vectorScale
                    val vx = cx + (lx * scaleX)
                    val vy = cy - (ly * scaleY)

                    // Línea del vector
                    drawLine(
                        color = Color(0xFFDC2626),
                        start = Offset(cx, cy),
                        end = Offset(vx, vy),
                        strokeWidth = 2.5f
                    )

                    // Cabeza de flecha
                    val angle = atan2(cy - vy, vx - cx)
                    val arrowLen = 14f
                    val arrowAngle = 0.4f
                    val p1 = Offset(
                        vx - arrowLen * cos(angle - arrowAngle),
                        vy + arrowLen * sin(angle - arrowAngle)
                    )
                    val p2 = Offset(
                        vx - arrowLen * cos(angle + arrowAngle),
                        vy + arrowLen * sin(angle + arrowAngle)
                    )
                    val arrowPath = Path().apply {
                        moveTo(vx, vy)
                        lineTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                        close()
                    }
                    drawPath(arrowPath, color = Color(0xFFDC2626))

                    // Etiqueta de la variable
                    val labelOffset = 14f
                    val tx = vx + labelOffset * cos(angle)
                    val ty = vy - labelOffset * sin(angle)
                    drawContext.canvas.nativeCanvas.drawText(
                        vl.variable.nameEs.take(8),
                        tx,
                        ty,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#991B1B")
                            textSize = 19f
                            isFakeBoldText = true
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                        }
                    )
                }
            }

            // 3. Dibujar las 47 Provincias como puntos
            provinces.forEach { proj ->
                val px = cx + (proj.scores[0].toFloat() * scaleX)
                val py = cy - (proj.scores[1].toFloat() * scaleY)

                val isSelected = (selectedProvince?.province?.name == proj.province.name)
                val isCatholic = proj.province.catholic > 50.0

                val ptColor = when {
                    isSelected -> Color(0xFF4F46E5)
                    colorByCatholic && isCatholic -> Color(0xFFD97706)
                    colorByCatholic && !isCatholic -> Color(0xFF0F766E)
                    else -> Color(0xFF0F766E)
                }

                if (isSelected) {
                    drawCircle(
                        color = Color(0xFF4F46E5).copy(alpha = 0.3f),
                        radius = 16f,
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 8f,
                        center = Offset(px, py)
                    )
                }

                drawCircle(
                    color = ptColor,
                    radius = if (isSelected) 6f else 4.5f,
                    center = Offset(px, py)
                )

                // Si está seleccionada, dibujar nombre
                if (isSelected) {
                    drawContext.canvas.nativeCanvas.drawText(
                        proj.province.name,
                        px,
                        py - 12f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 21f
                            isFakeBoldText = true
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                        }
                    )
                }
            }
        }
    }
}
