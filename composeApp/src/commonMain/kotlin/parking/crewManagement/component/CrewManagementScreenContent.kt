package parking.crewManagement.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.CreateNewUserItem
import parking.crewManagement.interaction.CrewManagementEvent
import parking.crewManagement.interaction.CrewManagementEvent.UserSelected
import parking.crewManagement.interaction.CrewManagementScreenState

@Composable
fun CrewManagementScreenContent(
    uiState: CrewManagementScreenState.Content,
    onEvent: (CrewManagementEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            CreateNewUserItem { onEvent(CrewManagementEvent.CreateUserClick) }
        }
        items(
            items = uiState.users,
            key = { user -> user.id },
        ) { user ->
            UserItem(
                user = user,
                onUserClick = { onEvent(UserSelected(user)) },
            )
        }
    }
}
