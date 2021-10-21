package hat.auth.utils.ui

import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState

@Composable
@ExperimentalPermissionsApi
fun PermissionRequiredDialog(
    permission: String,
    permissionNotAvailableContent: @Composable () -> Unit,
    permissionNotGrantedContent: @Composable (PermissionState) -> Unit,
    permissionGrantedContent: @Composable () -> Unit,
) {
    val permissionState = rememberPermissionState(permission)
    PermissionRequired(
        permissionState = permissionState,
        permissionNotGrantedContent = { permissionNotGrantedContent(permissionState) },
        permissionNotAvailableContent = permissionNotAvailableContent,
        content = permissionGrantedContent
    )
}
