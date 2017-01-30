package com.doodeec.maroonfrog.ui.meal.detail;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.base.RxMvpBasePresenter;
import com.doodeec.maroonfrog.dagger.scope.ActivityScope;
import com.doodeec.maroonfrog.data.model.Addon;
import com.doodeec.maroonfrog.data.model.AddonAdapterItem;
import com.doodeec.maroonfrog.data.model.AddonCategory;
import com.doodeec.maroonfrog.data.model.Meal;
import com.doodeec.maroonfrog.data.provider.DataProvider;
import com.doodeec.maroonfrog.ui.widget.collection.ObservableAdapter;
import com.doodeec.maroonfrog.ui.widget.collection.ObservableAdapterFactory;
import com.doodeec.maroonfrog.util.HorizontalDividerDecoration;
import com.doodeec.maroonfrog.util.rx.RxUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
@ActivityScope
public class MealDetailPresenter extends RxMvpBasePresenter<MealDetailView> {

    private final DataProvider dataProvider;
    private final Scheduler mainScheduler;
    private final MealDetailFragment.StateHolder stateHolder;

    private final ObservableAdapter<AddonAdapterItem> adapter;
    private final AddonListSource source;
    private final ObservableAdapter<Addon> pickedAdapter;
    private final PickedAddonsSource pickedSource;

    private final CompositeSubscription mealSubscription = new CompositeSubscription();

    @Inject MealDetailPresenter(DataProvider dataProvider,
                                Scheduler mainScheduler,
                                ObservableAdapterFactory adapterFactory,
                                MealDetailFragment.StateHolder stateHolder) {
        this.dataProvider = dataProvider;
        this.mainScheduler = mainScheduler;
        this.stateHolder = stateHolder;

        this.source = new AddonListSource(Collections.emptyList());
        this.adapter = adapterFactory.create(source);
        this.pickedSource = new PickedAddonsSource(Collections.emptyList());
        this.pickedAdapter = adapterFactory.create(pickedSource);
    }

    @Override public void attachView(MealDetailView view) {
        super.attachView(view);

        String mealId = intentExtra.getString(MealDetailActivity.MEALID);

        add(dataProvider.observeMeal(mealId)
                .doOnSubscribe(() -> showLoading(R.string.loading))
                .doOnNext(x -> Timber.d("Selected meal: %s", x))
                .doOnNext(x -> hideLoading())
                .compose(RxUtils.handleErrorNever("Error observing selected product"))
                .subscribe(this::setMeal));

        add(dataProvider.observeErrors()
                .observeOn(mainScheduler)
                .subscribe(t -> showError(t.getMessage()), baseErrorHandler("mealDetail:observeErrors")));

        setupAdapterEvents();
        setupPickedSourceUpdates();
    }

    @Override public void detachView(boolean retainInstance) {
        mealSubscription.clear();
        super.detachView(retainInstance);
    }

    private void setupAdapterEvents() {
        add(adapter.onItemEvent()
                .ofType(ObservableAdapter.ObjectEvent.class)
                .map(Observable::just)
                .switchMap(x -> Observable.merge(
                        x.map(ObservableAdapter.ObjectEvent::getData)
                                .ofType(Addon.class)
                                .map(addon -> (Action0) () -> onAddonClicked(addon)),
                        x.map(ObservableAdapter.ObjectEvent::getData)
                                .ofType(AddonCategory.class)
                                .map(cat -> (Action0) () -> onAddonCategoryClicked(cat))
                ))
                .subscribe(Action0::call, baseErrorHandler("addonList:itemEvent")));

        add(pickedAdapter.onItemEvent()
                .ofType(ObservableAdapter.ObjectEvent.class)
                .map(ObservableAdapter.ObjectEvent::getData)
                .ofType(Addon.class)
                .subscribe(a -> stateHolder.togglePickedAddon(a.getId()), baseErrorHandler("pickedAddonList:itemEvent")));
    }

    private void setupPickedSourceUpdates() {
        add(Observable.combineLatest(
                dataProvider.observeAddons(),
                stateHolder.observePickedAddons(),
                (addonList, picked) -> Observable.from(addonList)
                        .filter(a -> picked.contains(a.getId()))
                        .toList())
                .flatMap(x -> x)
                .subscribeOn(Schedulers.computation())
                .doOnNext(x -> Timber.d("Picked addons updated: %s", x))
                .compose(RxUtils.handleErrorNever("Error observing picked addon items"))
                .observeOn(mainScheduler)
                .subscribe(pickedSource::setData, baseErrorHandler("pickedAddonList:observeSource")));
    }

    @SuppressWarnings("ConstantConditions") private void setMeal(Meal meal) {
        mealSubscription.clear();
        //TODO error message when null??
        if (meal == null) return;

        //prevent CalledFromWrongThread exceptions
        Observable.just(meal)
                .observeOn(mainScheduler)
                .subscribe(m -> {
                    if (isViewAttached()) {
                        getView().setName(m.getName());
                        getView().setDescription(m.getDescription());
                    }
                });

        if (meal.getAddons() == null) return;

        final Set<String> addons = new HashSet<String>(meal.getAddons());

        Observable<List<AddonAdapterItem>> groupedAddonsObservable = dataProvider.observeAddons()
                .flatMap(add -> Observable.from(add)
                        .filter(a -> addons.contains(a.getId()))
                        .groupBy(Addon::getCategory)
                        .concatMap(x -> x.ofType(AddonAdapterItem.class).startWith(x.getKey()))
                        .toList());

        mealSubscription.add(Observable.combineLatest(
                groupedAddonsObservable,
                stateHolder.observeExpandedIdentifier(),
                stateHolder.observePickedAddons(),
                (items, categoryId, picked) -> Observable.from(items)
                        .doOnNext(prepareExpandFlag(categoryId))
                        .doOnNext(prepareSelectionFlag(picked))
                        .filter(filterCollapsedItems(categoryId))
                        .toList())
                .flatMap(x -> x)
                .subscribeOn(Schedulers.computation())
                .doOnNext(x -> Timber.d("Addon items updated: %s", x))
                .compose(RxUtils.handleErrorNever("Error observing addon items"))
                .observeOn(mainScheduler)
                .subscribe(source::setData, baseErrorHandler("addonList:observeSource")));
    }

    private Action1<AddonAdapterItem> prepareExpandFlag(String expandedCatId) {
        return i -> {
            if (i instanceof AddonCategory) {
                ((AddonCategory) i).setExpanded(i.getId().equals(expandedCatId));
            }
        };
    }

    private Action1<AddonAdapterItem> prepareSelectionFlag(Set<String> pickedSet) {
        return i -> {
            if (i instanceof Addon) {
                ((Addon) i).setPicked(pickedSet.contains(i.getId()));
            }
        };
    }

    private Func1<AddonAdapterItem, Boolean> filterCollapsedItems(String expandedCatId) {
        return i -> (i instanceof AddonCategory) || addonBelongsToCategory(i, expandedCatId);
    }

    private boolean addonBelongsToCategory(AddonAdapterItem item, String id) {
        return (item instanceof Addon && ((Addon) item).getCategory().getId().equals(id));
    }

    void initAddonList(RecyclerView list) {
        list.setAdapter(adapter);
        //we don't want to add multiple decorators, so this is the simplest way
        if (list.getLayoutManager() == null) {
            list.setLayoutManager(new LinearLayoutManager(list.getContext()));
            list.addItemDecoration(new DividerItemDecoration(list.getContext(), DividerItemDecoration.VERTICAL));
        }
    }

    void initPickedAddonList(RecyclerView list) {
        list.setAdapter(pickedAdapter);
        if (list.getLayoutManager() == null) {
            list.setLayoutManager(new LinearLayoutManager(list.getContext(), LinearLayoutManager.HORIZONTAL, false));
            list.addItemDecoration(new HorizontalDividerDecoration(list.getContext(), R.dimen.small_padding));
        }
    }

    private void onAddonClicked(Addon addon) {
        stateHolder.togglePickedAddon(addon.getId());
    }

    private void onAddonCategoryClicked(AddonCategory category) {
        stateHolder.toggleExpanded(category.getId());
    }
}
