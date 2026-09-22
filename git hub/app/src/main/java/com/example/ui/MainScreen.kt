package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ScatterPlot
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.PcaEngine
import com.example.ui.components.BiplotView
import com.example.ui.components.DatasetExplorerView
import com.example.ui.components.PedagogyStepsView
import com.example.ui.components.ScreePlotView

sealed class NavTab(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    object ScreePlot : NavTab("Método del Codo", Icons.Default.AutoGraph, "tab_scree_plot")
    object Pedagogy : NavTab("Algoritmo PCA", Icons.Default.Psychology, "tab_pedagogy")
    object Biplot : NavTab("Biplot 2D", Icons.Default.ScatterPlot, "tab_biplot")
    object Dataset : NavTab("Dataset Swiss", Icons.Default.FolderOpen, "tab_dataset")
}

val NAV_TABS = listOf(
    NavTab.ScreePlot,
    NavTab.Pedagogy,
    NavTab.Biplot,
    NavTab.Dataset
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val pcaResult = remember { PcaEngine.computePca() }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "PCA • Dataset Swiss (1888)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                NAV_TABS.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title, maxLines = 1) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> ScreePlotView(pcaResult = pcaResult)
                1 -> PedagogyStepsView(pcaResult = pcaResult)
                2 -> BiplotView(pcaResult = pcaResult)
                3 -> DatasetExplorerView(pcaResult = pcaResult)
            }
        }
    }
}
