package th.co.cjexpress.store.sage.presentation.ui.screen.orderReceiveTote

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.DELAY_DEFAULT_TIME
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDataModel
import th.co.cjexpress.store.sage.presentation.activity.mainMenu.MainMenuActivity
import th.co.cjexpress.store.sage.presentation.activity.orderReceiveTote.OrderReceiveToteItemsActivity
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonBottomAppBar
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCircularProgressIndicatorDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonEmptyLazyColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedTextField
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonScreenContentColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTopAppBar
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote.OrderReceiveToteDocsViewModel
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce
import th.co.cjexpress.store.sage.utils.safeKeyboardActionWithDebounce

@Composable
fun OrderReceiveToteDocsScreen(viewModel: OrderReceiveToteDocsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val baseScreenState by viewModel.baseScreenState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.resetSdToteLD()
        viewModel.getSdToteDocsList()
        viewModel.logEventScreenView()
    }

    when (val state = baseScreenState) {
        is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
        is BaseScreenState.Error -> {
            viewModel.logEventAlertDialog(state.errorData.messageText)
            TachyonAlertDialog(dialogData = state.errorData)
        }

        is BaseScreenState.Success<*> -> {
            when (state.data) {
                is SdToteDocsDetailUIModel -> {
                    viewModel.setSdToteDetailDataLD(state.data)

                    context
                        .startActivity(
                            Intent(context, OrderReceiveToteItemsActivity::class.java),
                        ).also {
                            (context as ComponentActivity).finish()
                        }
                }
            }
        }

        else -> {}
    }

    OrderReceiveToteDocsScreenContainer(viewModel)
}

@Composable
private fun OrderReceiveToteDocsScreenContainer(viewModel: OrderReceiveToteDocsViewModel) {
    val context = LocalContext.current
    val baseScreenState by viewModel.baseScreenState.collectAsState()
    val showScanNewToteDialogState by viewModel.showScanNewToteDialogState.collectAsState()
    val sdToteDocsListState by viewModel.sdToteDocsList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarContainer {
                viewModel.logEventOnBackPressed()
            }
        },
        bottomBar = {
            TachyonBottomAppBar {
                BottomAppBarContainer(
                    showScanNewToteDialogState = showScanNewToteDialogState,
                    setShowScanNewToteDialogState = { isShow ->
                        viewModel.setShowScanNewToteDialogState(isShow)
                        if (!isShow) {
                            viewModel.hideKeyboard(context)
                        }
                    },
                    openTote = { toteCode ->
                        viewModel.openTote(toteCode)
                    },
                )
            }
        },
    ) { innerPadding ->
        TachyonScreenContentColumn(scaffoldInnerPadding = innerPadding) {
            DocsListContainer(
                baseScreenState = baseScreenState,
                sdToteDocsList = sdToteDocsListState,
                pullToRefreshCallback = {
                    viewModel.getSdToteDocsList()
                },
                onDocsSelected = { docNo ->
                    viewModel.searchByDocNo(docNo)
                },
            )
        }
    }
}

@Composable
private fun TopAppBarContainer(onNavigationIconClick: () -> Unit = {}) {
    val context = LocalContext.current

    TachyonTopAppBar(
        titleText = stringResource(id = R.string.text_check_product_in_tote),
        onNavigationIconClick = {
            onNavigationIconClick.invoke()
            context.startActivity(Intent(context, MainMenuActivity::class.java)).also {
                (context as ComponentActivity).finish()
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocsListContainer(
    baseScreenState: BaseScreenState?,
    sdToteDocsList: List<SdToteDataModel>,
    pullToRefreshCallback: () -> Unit,
    onDocsSelected: (String) -> Unit,
) {
    val pullState = rememberPullToRefreshState()
    val pullScope = rememberCoroutineScope()

    LaunchedEffect(baseScreenState) {
        if (baseScreenState !is BaseScreenState.Loading) {
            pullScope.launch {
                pullState.animateToHidden()
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._md),
    ) {
        TachyonText(
            text = stringResource(id = R.string.text_document_list, sdToteDocsList.size),
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
        )

        PullToRefreshBox(
            state = pullState,
            indicator =
                if (baseScreenState !is BaseScreenState.Loading) {
                    {
                        Indicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = false,
                            state = pullState,
                        )
                    }
                } else {
                    {}
                },
            isRefreshing = baseScreenState is BaseScreenState.Loading,
            onRefresh = {
                pullScope.launch {
                    pullToRefreshCallback()
                }
            },
        ) {
            if (sdToteDocsList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
                ) {
                    items(
                        items = sdToteDocsList,
                        key = { docs -> docs.documentNo },
                    ) { document ->
                        DocsDetailsItemContainer(
                            document = document,
                            onDocSelected = { docNo ->
                                onDocsSelected(docNo)
                            },
                        )
                    }
                }
            } else {
                TachyonEmptyLazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                    painter = painterResource(R.drawable.icon_empty_document),
                    description = "ไม่มีรายการเอกสาร",
                )
            }
        }
    }
}

@Composable
private fun DocsDetailsItemContainer(
    document: SdToteDataModel,
    onDocSelected: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .height((158 / LocalDensity.current.density).dp)
                .background(TachyonDictionary.surface.static._default2)
                .selectable(
                    selected = document.documentNo.isNotEmpty(),
                    onClick =
                        safeClickWithDebounce {
                            onDocSelected(document.documentNo)
                        },
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(TachyonDictionary.space.padding._md),
            verticalArrangement = Arrangement.Center,
        ) {
            TachyonText(
                text =
                    stringResource(
                        id = R.string.text_document_date_format,
                        document.documentDate,
                    ),
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )

            TachyonText(
                text = stringResource(id = R.string.text_title_tote_code, document.toteCode),
                style = TachyonDictionary.body._1,
                color = TachyonDictionary.content._default,
            )

            TachyonText(
                text = stringResource(id = R.string.label_doc_sd_no, document.documentNo),
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )
        }
    }
}

@Composable
private fun BottomAppBarContainer(
    showScanNewToteDialogState: Boolean = false,
    setShowScanNewToteDialogState: (Boolean) -> Unit,
    openTote: (String) -> Unit,
) {
    if (showScanNewToteDialogState) {
        ScanNewToteDialog(
            dismissNewToteDialog = {
                setShowScanNewToteDialogState(false)
            },
            onConfirm = { toteCode ->
                openTote(toteCode)
            },
        )
    }

    TachyonSolidButton(
        modifier = Modifier.fillMaxWidth(),
        buttonText = stringResource(id = R.string.text_start_new_tote),
        style = TachyonSolidButtonStyle.GENERAL,
    ) {
        setShowScanNewToteDialogState(true)
    }
}

@Composable
private fun ScanNewToteDialog(
    dismissNewToteDialog: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var inputToteCode by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(DELAY_DEFAULT_TIME)
        focusRequester.requestFocus()
    }

    AlertDialog(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(TachyonDictionary.space.padding._md),
        properties =
            DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false,
            ),
        containerColor = TachyonDictionary.surface.static._default1,
        onDismissRequest = {
            dismissNewToteDialog()
        },
        title = {
            TachyonText(
                text = stringResource(id = R.string.text_scan_tote),
                style = TachyonDictionary.headLine._2,
                color = TachyonDictionary.content._default,
            )
        },
        text = {
            TachyonOutlinedTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                value = inputToteCode,
                onValueChange = { inputToteCode = it },
                placeholder = {
                    TachyonText(
                        text = stringResource(id = R.string.text_tote_number),
                        style = TachyonDictionary.caption._1,
                        color = TachyonDictionary.content._placeholder,
                    )
                },
                trailingIcon = {
                    if (inputToteCode.isNotEmpty()) {
                        IconButton(
                            onClick = safeClickWithDebounce { inputToteCode = "" },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "ล้างค่า",
                            )
                        }
                    }
                },
                keyboardActions =
                    KeyboardActions(
                        onDone =
                            safeKeyboardActionWithDebounce {
                                onConfirm(inputToteCode)
                                dismissNewToteDialog()
                            },
                    ),
            )
        },
        dismissButton = {
            TachyonOutlinedButton(
                buttonText = stringResource(id = R.string.text_cancel),
                style = TachyonOutlinedButtonStyle.GENERAL,
                onClick =
                    safeClickWithDebounce {
                        dismissNewToteDialog()
                    },
            )
        },
        confirmButton = {
            TachyonSolidButton(
                buttonText = stringResource(id = R.string.text_ok),
                style = TachyonSolidButtonStyle.GENERAL,
                onClick =
                    safeClickWithDebounce {
                        onConfirm(inputToteCode)
                        dismissNewToteDialog()
                    },
            )
        },
    )
}
