package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun ToDoRowItem(
	item: ToDoItem,
	onCheckedChange: (Boolean) -> Unit,
	onDeleteClick: () -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Box(
				modifier = Modifier
					.size(12.dp)
					.background(item.priority.color, shape = RoundedCornerShape(6.dp))
			)

			Spacer(modifier = Modifier.width(12.dp))

			Checkbox(
				checked = item.isCompleted,
				onCheckedChange = onCheckedChange
			)

			Text(
				text = item.title,
				style = MaterialTheme.typography.bodyLarge.copy(
					textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
				),
				color = if (item.isCompleted) Color.Gray else Color.Unspecified,
				modifier = Modifier.weight(1f)
			)

			IconButton(onClick = onDeleteClick) {
				Icon(
					imageVector = Icons.Default.Delete,
					contentDescription = "Remove Task item from data array",
					tint = MaterialTheme.colorScheme.error
				)
			}
		}
	}
}