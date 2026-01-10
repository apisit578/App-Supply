package th.co.cjexpress.store.sage.presentation.ui.screen.requestDiscountRtc

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.RequestDiscountType
import th.co.cjexpress.store.sage.presentation.activity.mainMenu.MainMenuActivity
import th.co.cjexpress.store.sage.presentation.activity.requestDiscountRtc.RequestDiscountItemsActivity
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentItemUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdSearchTypeUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonBottomAppBar
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCircularProgressIndicatorDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonEmptyLazyColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonScreenContentColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTopAppBar
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc.RequestDiscountDocsViewModel
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

@Composable
fun RequestDiscountDocsScreen(viewModel: RequestDiscountDocsViewModel = hiltViewModel()) {
    val baseScreenState by viewModel.baseScreenState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.logEventScreenView()
        viewModel.resetDocsDataLD()
    }

    when (val state = baseScreenState) {
        is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
        is BaseScreenState.Error -> {
            viewModel.logEventAlertDialog(state.errorData.messageText)
            TachyonAlertDialog(dialogData = state.errorData)
        }

        is BaseScreenState.Success<*> -> {
            when (state.data) {
                is RequestDiscountDocsViewModel.UIState.GetBdDocumentNoDetailBdState -> {
                    state.data.data?.let { viewModel.setDocsDataLD(it) }
                    context
                        .startActivity(Intent(context, RequestDiscountItemsActivity::class.java))
                        .also {
                            (context as ComponentActivity).finish()
                        }
                }
            }
        }

        else -> {}
    }

    RequestDiscountDocsScreenContainer(
        onBackPress = {
            viewModel.logEventOnBackPressed()
            context.startActivity(Intent(context, MainMenuActivity::class.java)).also {
                (context as ComponentActivity).finish()
            }
        },
    )
}

@Composable
fun RequestDiscountDocsScreenContainer(onBackPress: () -> Unit = {}) {
    Scaffold(
        topBar = {
            RequestDiscountDocsScreen_TopAppBarContainer(
                onBackPress = onBackPress,
            )
        },
        bottomBar = {
            TachyonBottomAppBar {
                BottomAppBarContainer()
            }
        },
    ) { innerPadding ->
        TachyonScreenContentColumn(scaffoldInnerPadding = innerPadding) {
            RequestDiscountDocsScreen_DocsListContainer()
        }
    }
}

@Composable
fun RequestDiscountDocsScreen_TopAppBarContainer(onBackPress: () -> Unit = {}) {
    TachyonTopAppBar(
        titleText = stringResource(id = R.string.text_discount_doc_title),
        onNavigationIconClick = {
            onBackPress.invoke()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDiscountDocsScreen_DocsListContainer(viewModel: RequestDiscountDocsViewModel = hiltViewModel()) {
    val getBdListState by viewModel.getBdListState.collectAsState()
    val pullState = rememberPullToRefreshState()
    val pullScope = rememberCoroutineScope()
    val baseScreenState by viewModel.baseScreenState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getBdList()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
    ) {
        TachyonText(
            text =
                stringResource(
                    R.string.text_document_list,
                    getBdListState.data?.documentListSize ?: 0,
                ),
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
                    viewModel.getBdList()
                }
            },
        ) {
            if (getBdListState.data?.data?.isNotEmpty() == true) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
                ) {
                    items(
                        items = getBdListState.data?.data.orEmpty(),
                        key = { docs -> docs.documentNumber },
                    ) { document ->
                        RequestDiscountDocsScreen_DocsDetailsContainer(
                            document = document,
                            onDocSelected = {
                                viewModel.logEventSelectRequestDiscountDoc(document)
                                viewModel.getBdDetail(it)
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
                    description = stringResource(R.string.text_docs_list_empty),
                )
            }
        }
    }
}

@Composable
fun RequestDiscountDocsScreen_DocsDetailsContainer(
    document: BdDocumentItemUIModel,
    onDocSelected: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .height((150 / LocalDensity.current.density).dp)
                .background(TachyonDictionary.surface.static._default2)
                .selectable(
                    selected = document.documentNumber.isNotEmpty(),
                    onClick =
                        safeClickWithDebounce {
                            onDocSelected(document.documentNumber)
                        },
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        start = TachyonDictionary.space.padding._md,
                        top = TachyonDictionary.space.padding._xs,
                        end = TachyonDictionary.space.padding._md,
                        bottom = TachyonDictionary.space.padding._xs,
                    ),
            verticalArrangement = Arrangement.Center,
        ) {
            TachyonText(
                text = stringResource(R.string.text_document_date_format, document.createdDate),
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )

            TachyonText(
                text =
                    stringResource(
                        R.string.text_format_shipment_document_no,
                        document.documentNumber,
                    ),
                style = TachyonDictionary.body._1,
                color = TachyonDictionary.content._default,
            )
        }
    }
}

@Composable
private fun BottomAppBarContainer(viewModel: RequestDiscountDocsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val getMasterMappingState by viewModel.getMasterMappingState.collectAsState()

    getMasterMappingState.data?.let {
        RequestDiscountDocsScreen_DiscountTypeDialog(
            discountTypes = it,
            onConfirm = { discountType ->
                viewModel.logEventSubmitDiscountType(discountType.name)
                context
                    .startActivity(Intent(context, RequestDiscountItemsActivity::class.java))
                    .also {
                        viewModel.resetDocsDataLD()
                        viewModel.setDiscountTypeLD(RequestDiscountType.valueOf(discountType.code))
                        (context as ComponentActivity).finish()
                    }
            },
        )
    }

    TachyonSolidButton(
        modifier = Modifier.fillMaxWidth(),
        buttonText = stringResource(R.string.text_create_doc),
        style = TachyonSolidButtonStyle.GENERAL,
        onClick = {
            viewModel.logEventOpenRequestDiscountDocsDialog()
            viewModel.getCampaignDiscountType()
        },
    )
}

@Composable
fun RequestDiscountDocsScreen_DiscountTypeDialog(
    viewModel: RequestDiscountDocsViewModel = hiltViewModel(),
    discountTypes: List<BdSearchTypeUIModel>,
    onConfirm: (BdSearchTypeUIModel) -> Unit,
) {
    var selectedType by remember { mutableStateOf<BdSearchTypeUIModel?>(null) }

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
            viewModel.resetGetMasterMappingState()
        },
        title = {
            TachyonText(
                text = stringResource(R.string.text_select_discount_type),
                style = TachyonDictionary.headLine._2,
                color = TachyonDictionary.content._default,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            LazyColumn {
                items(discountTypes) { discountType ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedType == discountType,
                                    onClick = safeClickWithDebounce { selectedType = discountType },
                                ).padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._md),
                    ) {
                        RadioButton(
                            selected = selectedType == discountType,
                            onClick = null,
                            colors =
                                RadioButtonDefaults.colors(
                                    selectedColor = TachyonDictionary.surface.action.primary._default,
                                ),
                        )

                        TachyonText(
                            text = discountType.name,
                            style = TachyonDictionary.body._1,
                            color = TachyonDictionary.content._default,
                        )
                    }
                }
            }
        },
        dismissButton = {
            TachyonOutlinedButton(
                buttonText = stringResource(id = R.string.text_cancel),
                style = TachyonOutlinedButtonStyle.GENERAL,
                onClick =
                    safeClickWithDebounce {
                        viewModel.resetGetMasterMappingState()
                    },
            )
        },
        confirmButton = {
            TachyonSolidButton(
                buttonText = stringResource(id = R.string.text_ok),
                style = TachyonSolidButtonStyle.GENERAL,
                enabled = selectedType != null,
                onClick =
                    safeClickWithDebounce { selectedType?.let { onConfirm(it) } },
            )
        },
    )
}
