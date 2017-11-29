package fr.allycs.app.View.HostDiscovery;


public class                            BottomSheetSettingsHostDiscovery {
  /*  private String                      TAG = "SettingsHostDiscovery";
    private View                        bottomSheet;
    private HostDiscoveryActivity       activity;

    public BottomSheetSettingsHostDiscovery(HostDiscoveryActivity activity, CoordinatorLayout coordinatorLayout) {
        this.activity = activity;
        Log.d(TAG, "menu created");
        View bottomSheet = new BottomSheetBuilder(activity, coordinatorLayout)
                .setMode(BottomSheetBuilder.MODE_LIST)
                //.setBackgroundColor(ContextCompat.getColor(activity, R.color.material_light_white))
                .setMenu(R.menu.settings_hostdiscovery)
                .setItemClickListener(onItemClick())
                .createView();
        bottomSheet.setVisibility(View.VISIBLE);
    }

    private BottomSheetItemClickListener onItemClick() {
        return new BottomSheetItemClickListener() {
            @Override
            public void onBottomSheetItemClick(MenuItem menuItem) {
                switch (menuItem.getTitle().toString()) {
                    case "Os filter":
                        activity.osFilterDialog();
                        break;
                    case "Select all targets":
                        activity.mHostAdapter.selectAll();
                        break;
                    case "Mode offline":
                        activity.startActivity(new Intent(activity, MenuActivity.class));
                        break;
                }
            }
        };
    }*/
}
